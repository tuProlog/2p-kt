package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal data class LazyCursor<T>(val iterator: Iterator<T>) : AbstractCursor<T>() {

    private val wrapped: Cursor<T> by lazy {
        NonLastCursor(
            iterator
        )
    }

    override val next: Cursor<out T>
        get() = wrapped.next

    override val current: T?
        get() = wrapped.current

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}
