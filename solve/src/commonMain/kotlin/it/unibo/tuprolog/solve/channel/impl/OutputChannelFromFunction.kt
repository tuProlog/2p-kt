package it.unibo.tuprolog.solve.channel.impl

internal class OutputChannelFromFunction<T : Any>(
    private val consumer: (T) -> Unit,
) : AbstractOutputChannel<T>() {
    override fun writeActually(value: T) = consumer(value)

    override fun flushActually() = Unit
}
