package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.Channel
import it.unibo.tuprolog.solve.channel.Listener

abstract class AbstractChannel<T> : Channel<T> {
    private val _listeners: MutableList<Listener<T>> = mutableListOf()

    override val listeners: Collection<Listener<T>>
        get() = _listeners.toList()

    override val countListeners: Int
        get() = _listeners.size

    override fun addListener(listener: Listener<T>) {
        _listeners.add(listener)
    }

    override fun removeListener(listener: Listener<T>) {
        _listeners.remove(listener)
    }

    override fun clearListeners() {
        _listeners.clear()
    }

}