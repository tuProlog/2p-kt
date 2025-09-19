package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.utils.synchronizedOnSelf

abstract class AbstractOutputChannel<T : Any> :
    AbstractChannel<T>(),
    OutputChannel<T> {
    final override fun write(value: T) =
        synchronizedOnSelf {
            if (isClosed) throw IllegalStateException("Output channel is closed")
            writeActually(value)
            notify(value)
        }

    protected abstract fun writeActually(value: T)

    override fun toString(): String = "${this::class.simpleName}(id=$id, closed=$isClosed)"

    override val streamTerm: Struct by lazy { OutputChannel.streamTerm(id) }

    final override fun flush() =
        synchronizedOnSelf {
            if (isClosed) throw IllegalStateException("Output channel is closed")
            flushActually()
        }

    protected abstract fun flushActually()
}
