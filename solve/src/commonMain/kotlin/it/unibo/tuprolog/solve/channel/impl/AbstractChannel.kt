package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.Listener
import it.unibo.tuprolog.utils.synchronizedOnSelf
import kotlin.concurrent.Volatile

abstract class AbstractChannel<T : Any> : Channel<T> {
    companion object {
        @Volatile
        private var instanceCount: Long = 0

        private fun nextId(): String = (instanceCount++).toString(16).padStart(16, '0')
    }

    @Suppress("ktlint:standard:property-naming", "ktlint:standard:backing-property-naming")
    private val _listeners: MutableList<Listener<T?>> = mutableListOf()

    protected val id = nextId()

    override fun addListener(listener: Listener<T?>): Unit =
        synchronizedOnSelf {
            _listeners.add(listener)
        }

    override fun removeListener(listener: Listener<T?>): Unit =
        synchronizedOnSelf {
            _listeners.remove(listener)
        }

    override fun clearListeners() =
        synchronizedOnSelf {
            _listeners.clear()
        }

    protected fun notify(value: T?) {
        _listeners.forEach { it(value) }
    }

    override fun close() =
        synchronizedOnSelf {
            if (!isClosed) {
                isClosed = true
            } else {
                throw IllegalStateException("Channel is already closed")
            }
        }

    final override var isClosed: Boolean = false
        get() = synchronizedOnSelf { field }
        private set
}
