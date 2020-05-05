package it.unibo.tuprolog.utils

internal abstract class AbstractCursor<T> : Cursor<T> {
    override fun toString(): String {
        return when {
            this.isOver -> "[]"
            !this.hasNext -> "[$current]"
            else -> "[$current, ...]"
        }
    }
}

