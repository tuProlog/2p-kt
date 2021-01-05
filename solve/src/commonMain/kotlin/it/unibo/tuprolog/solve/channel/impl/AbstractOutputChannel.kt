package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.OutputChannel
import kotlin.jvm.Synchronized

abstract class AbstractOutputChannel<T : Any> : AbstractChannel<T>(), OutputChannel<T> {
    @Synchronized
    final override fun write(value: T) {
        if (isClosed) throw IllegalStateException("Output channel is closed")
        writeActually(value)
        notify(value)
    }

    protected abstract fun writeActually(value: T)

    override fun toString(): String = "${this::class.simpleName}(id=$id, closed=$isClosed)"

    override val streamTerm: Struct
        get() = Struct.of("stream", Atom.of("out"), Atom.of(id))
}
