package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal abstract class AbstractCursor<T> : Cursor<T> {
    override fun toString(): String =
        when {
            this.isOver -> "[]"
            !this.hasNext -> "[$current]"
            else -> "[$current, ...]"
        }

    override fun iterator(): Iterator<T> =
        object : Iterator<T> {
            private var current: Cursor<out T> = this@AbstractCursor

            override fun hasNext(): Boolean = !current.isOver

            override fun next(): T {
                val result = current.current
                current = current.next
                return result!!
            }
        }

    abstract override val next: AbstractCursor<out T>

    override val isLazy: Boolean
        get() = false

    override fun <R> map(mapper: (T) -> R): AbstractCursor<out R> = MapperCursor(this, mapper)

    companion object {
        fun <T> of(source: Iterator<T>): AbstractCursor<out T> =
            if (source.hasNext()) {
                LazyCursor(source)
            } else {
                EmptyCursor
            }
    }
}
