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

class JsUrl : Url {
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

    override fun readAsText(): String =
        when {
            isFile && isNode -> readLocalFile { it.readUtf8() }
            isFile -> readText(path)
            else -> fetch(url, "UTF-8")
        }

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
