package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.Listener
import it.unibo.tuprolog.utils.toMutableThreadSafe
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile

abstract class AbstractChannel<T : Any> : Channel<T> {

    companion object {
        @Volatile
        private var instanceCount: Long = 0

        @Synchronized
        private fun nextId(): String = (instanceCount++).toString(16).padStart(16, '0')
    }

    override val extensions: MutableMap<String, Any> = mutableMapOf<String, Any>().toMutableThreadSafe()

    private val _listeners: MutableList<Listener<T?>> = mutableListOf()

    protected val id = nextId()

    @Synchronized
    override fun addListener(listener: Listener<T?>) {
        _listeners.add(listener)
    }

    @Synchronized
    override fun removeListener(listener: Listener<T?>) {
        _listeners.remove(listener)
    }

    @Synchronized
    override fun clearListeners() {
        _listeners.clear()
    }

    protected fun notify(value: T?) {
        _listeners.forEach { it(value) }
    }

    @Synchronized
    override fun close() {
        if (!isClosed) {
            isClosed = true
        } else {
            throw IllegalStateException("Channel is already closed")
        }
    }

    final override var isClosed: Boolean = false
        @Synchronized get
        private set
}
