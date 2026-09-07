package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.utils.synchronizedOnSelf

/**
 * Base [OutputChannel] implementation, handling [isClosed] checks and listener notification around [write]/[flush]
 * on top of [AbstractChannel], so that subclasses only need to implement the actual transport writes in
 * [writeActually]/[flushActually].
 */
abstract class AbstractOutputChannel<T : Any> :
    AbstractChannel<T>(),
    OutputChannel<T> {
    /** @throws IllegalStateException if this channel [it.unibo.tuprolog.solve.channel.Channel.isClosed]. */
    final override fun write(value: T) =
        synchronizedOnSelf {
            if (isClosed) throw IllegalStateException("Output channel is closed")
            writeActually(value)
            notify(value)
        }

    /** Writes [value] to the underlying transport. */
    protected abstract fun writeActually(value: T)

    override fun toString(): String = "${this::class.simpleName}(id=$id, closed=$isClosed)"

    override val streamTerm: Struct by lazy { OutputChannel.streamTerm(id) }

    /** @throws IllegalStateException if this channel [it.unibo.tuprolog.solve.channel.Channel.isClosed]. */
    final override fun flush() =
        synchronizedOnSelf {
            if (isClosed) throw IllegalStateException("Output channel is closed")
            flushActually()
        }

    /** Flushes any buffered output on the underlying transport. */
    protected abstract fun flushActually()
}
