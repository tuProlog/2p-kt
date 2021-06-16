package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal data class MapperCursor<T, R>(
    private val wrapped: Cursor<out T>,
    private val mapper: (T) -> R
) : AbstractCursor<R>() {
    override val next: Cursor<out R>
        get() = MapperCursor(wrapped.next, mapper)

    override val current: R?
        get() = wrapped.current?.let(mapper)

    override val hasNext: Boolean
        get() = wrapped.hasNext

    override val isOver: Boolean
        get() = wrapped.isOver

    override fun toString(): String {
        return super<AbstractCursor>.toString()
    }
}
