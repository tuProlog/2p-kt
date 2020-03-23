package it.unibo.tuprolog.solve.channel

interface Channel<T> {
    companion object {
        val stdin: InputChannel<String>
            get() = InputChannel.stdin

        val stdout: OutputChannel<String>
            get() = OutputChannel.stdout
    }

    val listeners: Collection<Listener<T>>

    val countListeners: Int
    fun addListener(listener: Listener<T>)
    fun removeListener(listener: Listener<T>)

    fun clearListeners()
}