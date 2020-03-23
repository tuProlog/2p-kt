package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.InputChannelFromFunction

interface InputChannel<T> : Channel<T> {
    companion object {
        val stdin: InputChannel<String>
            get() = stdin()

        fun <T> of(generator: () -> T, availabilityChecker: () -> Boolean = { true }): InputChannel<T> =
            InputChannelFromFunction(generator, availabilityChecker)
    }

    val available: Boolean

    fun read(): T
}