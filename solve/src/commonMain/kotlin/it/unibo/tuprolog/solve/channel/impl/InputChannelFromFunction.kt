package it.unibo.tuprolog.solve.channel.impl

internal class InputChannelFromFunction<T>(
    private val generator: () -> T,
    private val availabilityChecker: () -> Boolean
) : AbstractInputChannel<T>() {

    override val available: Boolean
        get() = availabilityChecker()

    override fun readActually(): T = generator()
}