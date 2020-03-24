package it.unibo.tuprolog.solve.channel

interface Channel<T> {
    fun addListener(listener: Listener<T>)
    fun removeListener(listener: Listener<T>)
    fun clearListeners()
}