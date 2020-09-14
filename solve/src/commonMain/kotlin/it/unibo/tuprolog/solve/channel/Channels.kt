package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.exception.PrologWarning

internal expect fun stdin(): InputChannel<String>

internal expect fun <T> stdout(): OutputChannel<T>

internal expect fun <T> stderr(): OutputChannel<T>

internal expect fun warning(): OutputChannel<PrologWarning>
