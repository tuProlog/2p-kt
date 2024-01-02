package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.InputChannelFromString
import it.unibo.tuprolog.solve.exception.Warning

internal actual fun stdin(): InputChannel<String> =
    InputChannel.of { throw IllegalStateException("No default implementation of stdin on JS") }

internal actual fun <T : Any> stderr(): OutputChannel<T> = OutputChannel.of { console.error(it) }

internal actual fun <T : Any> stdout(): OutputChannel<T> = OutputChannel.of { print(it) }

internal actual fun warning(): OutputChannel<Warning> = OutputChannel.of { console.warn(it.message) }

internal actual fun stringInputChannel(string: String): InputChannel<String> = InputChannelFromString(string)
