package it.unibo.tuprolog.solve.channel

actual fun stdin(): InputChannel<String> {
    return InputStreamChannel(System.`in`)
}
