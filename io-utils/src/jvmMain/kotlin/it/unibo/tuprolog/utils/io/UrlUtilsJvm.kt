package it.unibo.tuprolog.utils.io

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

actual fun Url.toFile(): File = JvmFile(toURL().file)

actual fun File.toUrl(): Url = Url.file(path)
