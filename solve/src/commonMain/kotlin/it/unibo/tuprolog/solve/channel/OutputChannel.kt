package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.OutputChannelFromFunction

interface OutputChannel<T> : Channel<T> {
    companion object {
        fun <T> stdOut(): OutputChannel<T> = stdout()

        fun <T> stdErr(): OutputChannel<T> = stderr()

        fun <T> of(consumer: (T) -> Unit): OutputChannel<T> = OutputChannelFromFunction(consumer)
    }

    fun write(value: T)
}