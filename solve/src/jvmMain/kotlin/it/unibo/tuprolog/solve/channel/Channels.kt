package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.exception.PrologWarning

internal actual fun stdin(): InputChannel<String> {
    return ReaderChannel(System.`in`)
}

internal actual fun <T : Any> stderr(): OutputChannel<T> {
    return PrintStreamChannel(System.err)
}

internal actual fun <T : Any> stdout(): OutputChannel<T> {
    return PrintStreamChannel(System.out)
}

internal actual fun warning(): OutputChannel<PrologWarning> {
    return OutputChannel.of { System.err.println(it.message) }
}
