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

    override fun <R> map(mapper: (T) -> R): Cursor<out R> {
        return MapperCursor(this, mapper)
    }
}
