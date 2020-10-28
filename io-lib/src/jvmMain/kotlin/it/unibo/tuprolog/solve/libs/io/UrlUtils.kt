package it.unibo.tuprolog.solve.libs.io

actual fun parseUrl(string: String): Url = JvmUrl(string)

actual fun fileUrl(path: String): Url = JvmUrl(protocol = "file", path = path)

actual fun remoteUrl(protocol: String, host: String, port: Int?, path: String, query: String?): Url =
    JvmUrl(protocol, host, port, path, query)
