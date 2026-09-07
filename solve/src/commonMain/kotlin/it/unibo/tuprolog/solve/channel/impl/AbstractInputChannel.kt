package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.synchronizedOnSelf
import it.unibo.tuprolog.utils.takeFirst

/**
 * Base [InputChannel] implementation, adding a one-element lookahead [queue] on top of [AbstractChannel] so that
 * [peek] and [isOver] both work without consuming an element, while subclasses only need to implement the actual
 * blocking read in [readActually].
 */
abstract class AbstractInputChannel<T : Any> :
    AbstractChannel<T>(),
    InputChannel<T> {
    private val queue: MutableList<T?> = dequeOf()

    /** @throws IllegalStateException if this channel [it.unibo.tuprolog.solve.channel.Channel.isClosed]. */
    override val available: Boolean
        get() = synchronizedOnSelf { if (isClosed) throw IllegalStateException("Input channel is closed") else true }

    /** Reads and returns the next element from the underlying transport, or `null` once exhausted; may block. */
    protected abstract fun readActually(): T?

    /** @throws IllegalStateException if this channel [it.unibo.tuprolog.solve.channel.Channel.isClosed]. */
    final override fun read(): T? =
        synchronizedOnSelf {
            if (isClosed) throw IllegalStateException("Input channel is closed")
            refillQueueIfNecessary()
            val read = queue.takeFirst()
            notify(read)
            read
        }

    /** @throws IllegalStateException if this channel [it.unibo.tuprolog.solve.channel.Channel.isClosed]. */
    final override fun peek(): T? =
        synchronizedOnSelf {
            if (isClosed) throw IllegalStateException("Input channel is closed")
            refillQueueIfNecessary()
            queue[0]
        }

    private fun refillQueue() {
        queue.add(readActually())
    }

    private fun refillQueueIfNecessary() {
        if (queue.isEmpty()) {
            refillQueue()
        }
    }

    /** @throws IllegalStateException if this channel [it.unibo.tuprolog.solve.channel.Channel.isClosed]. */
    override val isOver: Boolean
        get() =
            synchronizedOnSelf {
                if (isClosed) throw IllegalStateException("Input channel is closed")
                refillQueueIfNecessary()
                queue.first() == null
            }

    override fun toString(): String = "${this::class.simpleName}(id=$id, available=$available, closed=$isClosed)"

    override val streamTerm: Struct by lazy { InputChannel.streamTerm(id) }
}
