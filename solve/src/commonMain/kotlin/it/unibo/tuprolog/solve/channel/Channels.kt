package it.unibo.tuprolog.solve.channel

internal expect fun stdin(): InputChannel<String>

internal expect fun stdout(): OutputChannel<String>

internal expect fun stderr(): OutputChannel<String>