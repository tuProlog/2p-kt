package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.utils.dequeOf
import it.unibo.tuprolog.utils.synchronizedOnSelf
import it.unibo.tuprolog.utils.takeFirst

abstract class AbstractInputChannel<T : Any> :
    AbstractChannel<T>(),
    InputChannel<T> {
    private val queue: MutableList<T?> = dequeOf()

    override val available: Boolean
        get() = synchronizedOnSelf { if (isClosed) throw IllegalStateException("Input channel is closed") else true }

    protected abstract fun readActually(): T?

    final override fun read(): T? =
        synchronizedOnSelf {
            if (isClosed) throw IllegalStateException("Input channel is closed")
            refillQueueIfNecessary()
            val read = queue.takeFirst()
            notify(read)
            read
        }

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
