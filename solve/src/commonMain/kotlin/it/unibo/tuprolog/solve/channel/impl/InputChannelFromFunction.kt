package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.utils.synchronizedOnSelf

internal class InputChannelFromFunction<T : Any>(
    private val generator: () -> T?,
    private val availabilityChecker: () -> Boolean,
) : AbstractInputChannel<T>() {
    override val available: Boolean
        get() = synchronizedOnSelf { availabilityChecker() }

    override fun readActually(): T? = generator()
}
