package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException

/** @throws it.unibo.tuprolog.solve.libs.io.exceptions.InvalidUrlException if [string] is not a well-formed URL. */
actual fun parseUrl(string: String): Url = JsUrl(string)

actual fun fileUrl(path: String): Url = JsUrl(protocol = "file", path = path)

actual fun remoteUrl(
    protocol: String,
    host: String,
    port: Int?,
    path: String,
    query: String?,
): Url = JsUrl(protocol, host, port, path, query)

/**
 * JS implementation of [it.unibo.tuprolog.solve.libs.io.openInputChannel]: eagerly [Url.readAsText]s the whole
 * resource and wraps it into an in-memory [InputChannel], rather than streaming it lazily as the JVM implementation does.
 * @throws IOException if the resource cannot be read.
 */
actual fun Url.openInputChannel(): InputChannel<String> = InputChannel.of(readAsText())

/**
 * JS implementation of [it.unibo.tuprolog.solve.libs.io.openOutputChannel]: unsupported on this platform.
 * @throws IOException unconditionally.
 */
actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> =
    throw IOException("Writing not supported for ${toString()}")
