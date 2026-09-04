package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.channel.SinkOutputChannel
import it.unibo.tuprolog.solve.libs.io.channel.SourceInputChannel
import it.unibo.tuprolog.solve.libs.io.exceptions.IOException
import okio.FileSystem
import okio.NodeJsFileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

internal actual val platformFileSystem: FileSystem = NodeJsFileSystem

// Node's own file:-URL <-> native-path conversion: correctly handles Windows drive letters,
// backslash-vs-forward-slash, and percent-decoding, so `toLocalPath` doesn't have to.
private val FILE_URL_TO_PATH: dynamic by lazy { js("require('url').fileURLToPath") }

actual fun parseUrl(string: String): Url = JsUrl(string)

actual fun fileUrl(path: String): Url = JsUrl(protocol = "file", path = path)

actual fun remoteUrl(
    protocol: String,
    host: String,
    port: Int?,
    path: String,
    query: String?,
): Url = JsUrl(protocol, host, port, path, query)

internal actual fun Url.toLocalPath(): Path = (FILE_URL_TO_PATH(toString()) as String).toPath()

actual fun Url.openInputChannel(): InputChannel<String> =
    if (isFile && isNode) {
        SourceInputChannel(LocalFileSystem.source(toLocalPath()).buffer())
    } else {
        InputChannel.of(readAsText())
    }

actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> {
    if (!isFile || !isNode) {
        throw IOException("Writing not supported for ${toString()}")
    }
    val path = toLocalPath()
    val sink = if (append) LocalFileSystem.appendingSink(path) else LocalFileSystem.sink(path)
    return SinkOutputChannel(sink.buffer())
}
