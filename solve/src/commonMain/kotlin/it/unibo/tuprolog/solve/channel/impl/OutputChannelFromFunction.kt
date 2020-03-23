package it.unibo.tuprolog.solve.channel.impl

internal class OutputChannelFromFunction<T>(private val consumer: (T) -> Unit) : AbstractOutputChannel<T>() {
    override fun write(value: T) = consumer(value)
}