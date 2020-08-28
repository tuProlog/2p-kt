package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.exception.PrologWarning

internal actual fun stdin(): InputChannel<String> {
    return InputStreamChannel(System.`in`)
}

internal actual fun <T> stderr(): OutputChannel<T> {
    return OutputChannel.of { System.err.print(it) }
}

internal actual fun <T> stdout(): OutputChannel<T> {
    return OutputChannel.of { print(it) }
}

internal actual fun warning(): OutputChannel<PrologWarning> {
    return OutputChannel.of { System.err.println(it.message) }
}