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

actual fun parseUrl(string: String): Url = JvmUrl(string)

actual fun fileUrl(path: String): Url = JvmUrl(protocol = "file", path = path)

actual fun remoteUrl(
    protocol: String,
    host: String,
    port: Int?,
    path: String,
    query: String?,
): Url = JvmUrl(protocol, host, port, path, query)

fun URL.toUrl(): Url = parseUrl(toExternalForm())

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

actual fun Url.openInputChannel(): InputChannel<String> = ReaderChannel(toURL().openStream())

actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> {
    if (!isFile) {
        throw IOException("Writing not supported for ${toString()}")
    }
    val file = File(toURL().file)
    val outputStream = FileOutputStream(file, append)
    return WriterChannel(outputStream)
}
