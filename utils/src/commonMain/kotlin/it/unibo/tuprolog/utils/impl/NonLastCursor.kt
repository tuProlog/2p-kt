package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor
import it.unibo.tuprolog.utils.toCursor

internal class NonLastCursor<T>(source: Iterator<T>) : AbstractCursor<T>() {

    private var source: Iterator<T>? = source

    override val next: Cursor<out T> by lazy {
        this.source!!.toCursor().also { this.source = null }
    }

    override val current: T = source.next()

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}
