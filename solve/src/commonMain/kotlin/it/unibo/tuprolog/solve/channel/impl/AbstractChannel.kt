package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.Listener
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile

abstract class AbstractChannel<T> : Channel<T> {

    companion object {
        @Volatile
        private var instanceCount: Long = 0

        @Synchronized
        private fun nextId(): String = (instanceCount++).toString(16).padStart(16, '0')
    }

    private val _listeners: MutableList<Listener<T>> = mutableListOf()

    protected val id = nextId()

    override fun addListener(listener: Listener<T>) {
        _listeners.add(listener)
    }

    override fun removeListener(listener: Listener<T>) {
        _listeners.remove(listener)
    }

    override fun clearListeners() {
        _listeners.clear()
    }

    protected fun notify(value: T) {
        _listeners.forEach { it(value) }
    }

    override fun close() {
        if (!isClosed) {
            isClosed = true
        } else {
            throw IllegalStateException("Channel is already closed")
        }
    }

    final override var isClosed: Boolean = false
        private set
}
