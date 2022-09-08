package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.channel.ReaderChannel
import it.unibo.tuprolog.solve.channel.WriterChannel
import it.unibo.tuprolog.utils.io.Url
import it.unibo.tuprolog.utils.io.exceptions.IOException
import it.unibo.tuprolog.utils.io.toURL
import java.io.File
import java.io.FileOutputStream

actual fun Url.openInputChannel(): InputChannel<String> = ReaderChannel(toURL().openStream())

actual fun Url.openOutputChannel(append: Boolean): OutputChannel<String> {
    if (!isFile) {
        throw IOException("Writing not supported for ${toString()}")
    }
    val file = File(toURL().file)
    val outputStream = FileOutputStream(file, append)
    return WriterChannel(outputStream)
}
