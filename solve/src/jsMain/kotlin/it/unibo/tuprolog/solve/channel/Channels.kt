package it.unibo.tuprolog.solve.channel

actual fun stdin(): InputChannel<String> {
    throw IllegalStateException("No default implementation of stdin on JS")
}
