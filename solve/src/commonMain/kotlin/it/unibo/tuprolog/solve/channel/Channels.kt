package it.unibo.tuprolog.solve.channel

internal expect fun stdin(): InputChannel<String>

internal expect fun <T> stdout(): OutputChannel<T>

internal expect fun <T> stderr(): OutputChannel<T>