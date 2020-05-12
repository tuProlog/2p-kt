package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal data class MapperCursor<T, R>(val wrapped: Cursor<out T>, val mapper: (T) -> R) : AbstractCursor<R>() {
    override val next: Cursor<out R> by lazy {
        MapperCursor(
            wrapped.next,
            mapper
        )
    }

    override val current: R? by lazy {
        wrapped.current.let { if (it == null) null else mapper(it) }
    }

    override val hasNext: Boolean
        get() = wrapped.hasNext

    override val isOver: Boolean
        get() = wrapped.isOver

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}