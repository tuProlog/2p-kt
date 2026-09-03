package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.ReaderChannel
import it.unibo.tuprolog.solve.channel.WriterChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toOkioPath
import okio.buffer
import java.io.File
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL

internal actual val platformFileSystem: FileSystem = FileSystem.SYSTEM

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

internal actual fun Url.toLocalPath(): Path = File(toURL().file).toOkioPath()

actual fun Url.openInputChannel(): InputChannel<String> =
    if (isFile) {
        ReaderChannel(LocalFileSystem.source(toLocalPath()).buffer().inputStream())
    } else {
        ReaderChannel(toURL().openStream())
    }

actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> {
    if (!isFile) {
        throw IOException("Writing not supported for ${toString()}")
    }
    val path = toLocalPath()
    val sink = if (append) LocalFileSystem.appendingSink(path) else LocalFileSystem.sink(path)
    return WriterChannel(sink.buffer().outputStream())
}
