package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException

actual fun parseUrl(string: String): Url = JsUrl(string)

actual fun fileUrl(path: String): Url = JsUrl(protocol = "file", path = path)

actual fun remoteUrl(
    protocol: String,
    host: String,
    port: Int?,
    path: String,
    query: String?,
): Url = JsUrl(protocol, host, port, path, query)

actual fun Url.openInputChannel(): InputChannel<String> = InputChannel.of(readAsText())

actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> =
    throw IOException("Writing not supported for ${toString()}")
