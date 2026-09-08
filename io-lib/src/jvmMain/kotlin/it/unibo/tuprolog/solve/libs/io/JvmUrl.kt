package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URL
import kotlin.streams.asSequence

/**
 * JVM implementation of [Url], thinly wrapping a `java.net.` [url] (accessible for interop, e.g. with code that
 * needs a plain `java.net.URL`/`URI`).
 *
 * @param url the wrapped platform URL; see [toUrl]/[toURL] for conversions between it and [Url].
 */
data class JvmUrl(
    val url: URL,
) : Url {
    /** Parses [string] into a [JvmUrl], via [toUrl]. */
    constructor(string: String) : this(string.toUrl())

    /** Builds a [JvmUrl] from its [protocol]/[host]/[port]/[path]/[query] components, via [Url.toString]. */
    constructor(protocol: String, host: String = "", port: Int? = null, path: String = "", query: String? = null) :
        this(Url.toString(protocol, host, port, path, query))

    override val protocol: String
        get() = url.protocol

    override val host: String
        get() = url.host

    override val path: String
        get() = url.path

    override val port: Int?
        get() = url.port.let { if (it > 0) it else null }

    override val query: String?
        get() = url.query

    /** @throws IOException if [url] cannot be opened (missing file, unreachable host, ...),
     * wrapping the underlying `java.io.IOException`. */
    override fun readAsText(): String =
        try {
            BufferedReader(InputStreamReader(url.openStream())).lines().asSequence().joinToString("\n")
        } catch (e: FileNotFoundException) {
            throw IOException("Cannot find resource: $url", e)
        } catch (e: java.io.IOException) {
            throw IOException("Generic I/O error while accessing: $url", e)
        }

    /** @throws IOException if [url] cannot be opened, wrapping the underlying `java.io.IOException`. */
    override fun readAsByteArray(): ByteArray =
        try {
            BufferedInputStream(url.openStream()).readAllBytes()
        } catch (e: java.io.IOException) {
            throw IOException(e.message, e)
        }

    override fun toString(): String = url.toString()
}
