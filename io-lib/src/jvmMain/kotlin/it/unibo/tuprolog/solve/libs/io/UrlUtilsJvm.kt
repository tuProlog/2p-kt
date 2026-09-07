package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.ReaderChannel
import it.unibo.tuprolog.solve.channel.WriterChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import java.io.File
import java.io.FileOutputStream
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

/** @throws it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException if [string] is not a well-formed URL. */
actual fun parseUrl(string: String): Url = JvmUrl(string)

actual fun fileUrl(path: String): Url = JvmUrl(protocol = "file", path = path)

actual fun remoteUrl(
    protocol: String,
    host: String,
    port: Int?,
    path: String,
    query: String?,
): Url = JvmUrl(protocol, host, port, path, query)

/** Converts this `java.net.` [URL] into a 2P-Kt [Url], via [Url]'s string representation. */
fun URL.toUrl(): Url = parseUrl(toExternalForm())

/** Converts this [Url] into a `java.net.` [URL] (returning the wrapped one directly if this is already a [JvmUrl]). */
fun Url.toURL(): URL =
    when (this) {
        is JvmUrl -> url
        else -> toString().toUrl()
    }

internal fun String.toUrl(): URL =
    try {
        URI(this)
            .takeIf { it.isAbsolute }
            ?.toURL()
            ?: throw InvalidUrlException(message = "Invalid URL: $this")
    } catch (e: MalformedURLException) {
        throw InvalidUrlException(message = "Invalid URL: $this", cause = e)
    } catch (e: URISyntaxException) {
        throw InvalidUrlException(message = "Invalid URL: $this", cause = e)
    }

/**
 * JVM implementation of [it.unibo.tuprolog.solve.libs.io.openInputChannel]: opens a stream on this [Url] via
 * `java.net.URL.openStream`, working for any protocol the JVM itself supports (files, `http(s)`, `jar`, ...).
 * @throws it.unibo.tuprolog.solve.libs.io.exceptions.IOException if the underlying `java.net.URL` cannot be opened.
 */
actual fun Url.openInputChannel(): InputChannel<String> = ReaderChannel(toURL().openStream())

/**
 * JVM implementation of [it.unibo.tuprolog.solve.libs.io.openOutputChannel]: opens this [Url]'s [Url.isFile] path as
 * a `java.io.FileOutputStream`, positioned at the end of the file if [append].
 * @throws IOException if this [Url] is not a `file://` one (writing to remote URLs is not supported).
 */
actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> {
    if (!isFile) {
        throw IOException("Writing not supported for ${toString()}")
    }
    val file = File(toURL().file)
    val outputStream = FileOutputStream(file, append)
    return WriterChannel(outputStream)
}
