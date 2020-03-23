package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.InputChannel

abstract class AbstractInputChannel<T> : AbstractChannel<T>(), InputChannel<T> {

    override val available: Boolean
        get() = true
}