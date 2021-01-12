package it.unibo.tuprolog.solve.channel.impl

import kotlin.jvm.Synchronized

internal class InputChannelFromFunction<T : Any>(
    private val generator: () -> T?,
    private val availabilityChecker: () -> Boolean
) : AbstractInputChannel<T>() {

    override val available: Boolean
        @Synchronized
        get() = availabilityChecker()

    override fun readActually(): T? = generator()
}
