package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal abstract class AbstractCursor<T> : Cursor<T> {
    override fun toString(): String {
        return when {
            this.isOver -> "[]"
            !this.hasNext -> "[$current]"
            else -> "[$current, ...]"
        }
    }
}

