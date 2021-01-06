package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.ReaderChannel
import it.unibo.tuprolog.solve.channel.WriterChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import java.io.File
import java.io.FileOutputStream
import java.net.URL

actual fun parseUrl(string: String): Url = JvmUrl(string)

actual fun fileUrl(path: String): Url = JvmUrl(protocol = "file", path = path)

actual fun remoteUrl(protocol: String, host: String, port: Int?, path: String, query: String?): Url =
    JvmUrl(protocol, host, port, path, query)

fun URL.toUrl(): Url = parseUrl(toExternalForm())

fun Url.toURL(): URL = when (this) {
    is JvmUrl -> url
    else -> URL(toString())
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
