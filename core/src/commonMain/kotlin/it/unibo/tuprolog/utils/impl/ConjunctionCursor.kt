package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal data class ConjunctionCursor<T>(val first: Cursor<out T>, val second: Cursor<out T>) : AbstractCursor<T>() {
    override val isOver: Boolean
        get() = first.isOver && second.isOver

    override val next: Cursor<out T>
        get() = if (first.hasNext) ConjunctionCursor(
            first.next,
            second
        ) else second

    override val current: T?
        get() = if (first.hasNext) first.current else second.current

    override val hasNext: Boolean
        get() = first.hasNext || second.hasNext

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}