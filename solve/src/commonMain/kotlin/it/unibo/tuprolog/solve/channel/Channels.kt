package it.unibo.tuprolog.solve.channel

expect fun stdin(): InputChannel<String>

fun stdout(): OutputChannel<String> = OutputChannel.of { print(it) }