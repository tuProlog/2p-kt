package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.exception.Warning
import java.io.StringReader

internal actual fun stdin(): InputChannel<String> = ReaderChannel(System.`in`)

internal actual fun <T : Any> stderr(): OutputChannel<T> = PrintStreamChannel(System.err)

internal actual fun <T : Any> stdout(): OutputChannel<T> = PrintStreamChannel(System.out)

internal actual fun warning(): OutputChannel<Warning> =
    OutputChannel.of { System.err.println(it.message); System.err.flush() }

internal actual fun stringInputChannel(string: String): InputChannel<String> =
    ReaderChannel(StringReader(string))
