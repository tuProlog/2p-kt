package it.unibo.tuprolog.utils

internal data class NonLastCursor<T>(val iterator: Iterator<T>) : AbstractCursor<T>() {

    override val next: Cursor<out T> by lazy {
        iterator.toCursor()
    }

    override val current: T = iterator.next()

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}