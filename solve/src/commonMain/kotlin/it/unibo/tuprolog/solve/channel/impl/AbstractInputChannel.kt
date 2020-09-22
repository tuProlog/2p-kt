package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.InputChannel

abstract class AbstractInputChannel<T> : AbstractChannel<T>(), InputChannel<T> {

    override val available: Boolean
        get() = true

    protected abstract fun readActually(): T

    final override fun read(): T {
        val read = readActually()
        notify(read)
        return read
    }
}
