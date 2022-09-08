package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.utils.io.Url
import it.unibo.tuprolog.utils.io.exceptions.IOException

actual fun Url.openInputChannel(): InputChannel<String> =
    InputChannel.of(readAsText())

actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> {
    throw IOException("Writing not supported for ${toString()}")
}
