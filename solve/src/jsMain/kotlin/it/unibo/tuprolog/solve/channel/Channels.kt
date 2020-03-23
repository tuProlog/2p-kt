package it.unibo.tuprolog.solve.channel

actual fun stdin(): InputChannel<String> {
    return InputChannel.of {
        throw IllegalStateException("No default implementation of stdin on JS")
    }
}

actual fun stderr(): OutputChannel<String> {
    return OutputChannel.of {
        console.error(it)
    }
}

internal actual fun stdout(): OutputChannel<String> {
    return OutputChannel.of {
        print(it)
    }
}