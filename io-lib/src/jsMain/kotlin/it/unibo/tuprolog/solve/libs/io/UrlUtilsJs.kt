package it.unibo.tuprolog.solve.libs.io

actual fun parseUrl(string: String): Url = JsUrl(string)

actual fun fileUrl(path: String): Url = JsUrl(protocol = "file", path = path)

actual fun remoteUrl(protocol: String, host: String, port: Int?, path: String, query: String?): Url =
    JsUrl(protocol, host, port, path, query)
