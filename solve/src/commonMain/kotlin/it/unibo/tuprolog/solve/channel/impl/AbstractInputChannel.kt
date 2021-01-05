package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.InputChannel

abstract class AbstractInputChannel<T> : AbstractChannel<T>(), InputChannel<T> {

    override val available: Boolean
        get() = true

    protected abstract fun readActually(): T

    final override fun read(): T {
        if (isClosed) throw IllegalStateException("Input channel is closed")
        val read = readActually()
        notify(read)
        return read
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id, available=$available, closed=$isClosed)"

    override val streamTerm: Struct
        get() = Struct.of("stream", Atom.of("in"), Atom.of(id))
}
