package it.unibo.tuprolog.solve.channel

internal actual fun stdin(): InputChannel<String> {
    return InputStreamChannel(System.`in`)
}

internal actual fun <T> stderr(): OutputChannel<T> {
    return OutputChannel.of { System.err.print(it) }
}

internal actual fun <T> stdout(): OutputChannel<T> {
    return OutputChannel.of { System.out.print(it) }
}