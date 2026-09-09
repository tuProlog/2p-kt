@file:Suppress("TooGenericExceptionCaught")

package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import okio.buffer
import okio.use
import org.khronos.webgl.ArrayBuffer
import kotlin.js.JsName
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException as TuPrologIOException

/** Binding for the global WHATWG `URL`, available in both Node and browsers. */
@JsName("URL")
private external class JsNativeUrl(
    url: String,
) {
    val protocol: String
    val hostname: String
    val port: String
    val pathname: String
    val search: String
}

/**
 * JS implementation of [Url], hand-parsed via the WHATWG [JsNativeUrl] binding (there being no `java.net.URL`
 * equivalent to wrap, unlike [it.unibo.tuprolog.solve.libs.io.JvmUrl]).
 *
 * Its behavior further forks on [isNode]: [readAsText]/[readAsByteArray] read local ([isFile]) resources straight
 * off disk (via [LocalFileSystem]) only under Node, fall back to `window.localStorage` for local resources in a
 * browser, and otherwise eagerly [fetch] the remote resource in full, for both platforms.
 */
class JsUrl : Url {
    /**
     * Parses [url], e.g. `JsUrl("https://example.com/page")`.
     * @throws InvalidUrlException if [url] is not well-formed, including native Windows paths (e.g. `C:\Users\...`),
     * which [JsNativeUrl] would otherwise mis-parse as a URL with a single-letter scheme.
     */
    constructor(url: String) {
        val parsed =
            try {
                JsNativeUrl(url)
            } catch (e: Throwable) {
                throw InvalidUrlException("Invalid URL: $url", e)
            }
        // A native Windows path (e.g. `C:\Users\...`) parses "successfully" as a URL with a
        // single-letter scheme (the drive letter) and an opaque, unprocessed rest, since only
        // "special" schemes (http, file, ...) get `://`/backslash handling. No real scheme is a
        // single letter, so treat this as unparseable instead, forcing Url.of's file:// fallback.
        if (parsed.protocol.removeSuffix(":").length == 1) {
            throw InvalidUrlException("Invalid URL: $url")
        }
        protocol = parsed.protocol.removeSuffix(":")
        host = parsed.hostname
        path = parsed.pathname
        port = parsed.port.toIntOrNull()
        query = parsed.search.removePrefix("?").ifEmpty { null }
        this.url = url
    }

    /** Builds a [JsUrl] from its [protocol]/[host]/[port]/[path]/[query] components, via [Url.toString]. */
    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) {
        this.protocol = protocol
        this.host = host
        this.port = port
        this.path = path
        this.query = query
        this.url = Url.toString(protocol, host, port, path, query)
    }

    private val url: String

    override val protocol: String

    override val host: String

    override val path: String

    override val port: Int?

    override val query: String?

    /** @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if the resource cannot be read: missing
     * local file or Node error ([isFile] && [isNode]), missing `window.localStorage` entry ([isFile] in a
     * browser), or unreachable host (remote). */
    override fun readAsText(): String =
        when {
            isFile && isNode -> readLocalFile { it.readUtf8() }
            isFile -> readText(path)
            else -> fetch(url, "UTF-8")
        }

    /** @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if the resource cannot be read; see
     * [readAsText] for the per-case conditions. */
    override fun readAsByteArray(): ByteArray =
        when {
            isFile && isNode -> readLocalFile { it.readByteArray() }
            isFile -> readBin(path)
            else -> fetch<ArrayBuffer>(url).toByteArray()
        }

    private fun <T> readLocalFile(action: (okio.BufferedSource) -> T): T =
        try {
            LocalFileSystem.source(toLocalPath()).buffer().use(action)
        } catch (e: okio.IOException) {
            throw TuPrologIOException(e.message, e)
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as JsUrl

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int = url.hashCode()

    override fun toString(): String = url
}
