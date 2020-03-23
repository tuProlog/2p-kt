package it.unibo.tuprolog.solve.channel.impl

import it.unibo.tuprolog.solve.channel.OutputChannel

abstract class AbstractOutputChannel<T> : AbstractChannel<T>(), OutputChannel<T> {
    final override fun write(value: T) {
        writeActually(value)
        notify(value)
    }

    protected abstract fun writeActually(value: T)
}