package it.unibo.tuprolog.solve.channel

internal actual fun stdin(): InputChannel<String> {
    return InputChannel.of {
        throw IllegalStateException("No default implementation of stdin on JS")
    }
}

internal actual fun <T> stderr(): OutputChannel<T> {
    return OutputChannel.of {
        console.error(it)
    }
}

internal actual fun <T> stdout(): OutputChannel<T> {
    return OutputChannel.of {
        print(it)
    }
}