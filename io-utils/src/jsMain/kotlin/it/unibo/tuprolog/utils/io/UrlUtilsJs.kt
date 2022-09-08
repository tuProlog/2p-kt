package it.unibo.tuprolog.utils.io

actual fun parseUrl(string: String): Url = JsUrl(string)

actual fun fileUrl(path: String): Url = JsUrl(protocol = "file", path = path)

actual fun remoteUrl(protocol: String, host: String, port: Int?, path: String, query: String?): Url =
    JsUrl(protocol, host, port, path, query)

actual fun File.toUrl(): Url =
    when (this) {
        is JsFile -> url
        else -> Url.of(path)
    }

actual fun Url.toFile(): File = JsFile(this)
