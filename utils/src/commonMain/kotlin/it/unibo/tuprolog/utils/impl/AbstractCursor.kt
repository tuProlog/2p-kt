package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal abstract class AbstractCursor<T> : Cursor<T> {
    override fun toString(): String = when {
        this.isOver -> "[]"
        !this.hasNext -> "[$current]"
        else -> "[$current, ...]"
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        private var current: Cursor<out T> = this@AbstractCursor

        override fun hasNext(): Boolean = !current.isOver

        override fun next(): T {
            val result = current.current
            current = current.next
            return result!!
        }
    }

    override val isLazy: Boolean
        get() = false

    override fun <R> map(mapper: (T) -> R): Cursor<out R> = MapperCursor(this, mapper)
}
