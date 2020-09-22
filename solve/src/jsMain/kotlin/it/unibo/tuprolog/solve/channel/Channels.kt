package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.exception.PrologWarning

internal actual fun stdin(): InputChannel<String> =
    InputChannel.of { throw IllegalStateException("No default implementation of stdin on JS") }

internal actual fun <T> stderr(): OutputChannel<T> =
    OutputChannel.of { console.error(it) }

internal actual fun <T> stdout(): OutputChannel<T> =
    OutputChannel.of { print(it) }

internal actual fun warning(): OutputChannel<PrologWarning> =
    OutputChannel.of { console.warn(it.message) }
