package it.unibo.tuprolog.solve.channel

actual fun stdin(): InputChannel<String> {
    return InputStreamChannel(System.`in`)
}

actual fun stderr(): OutputChannel<String> {
    return OutputChannel.of { System.err.print(it) }
}

internal actual fun stdout(): OutputChannel<String> {
    return OutputChannel.of { System.out.print(it) }
}