package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal data class MapperCursor<T, R>(
    private val wrapped: Cursor<out T>,
    private val mapper: (T) -> R
) : AbstractCursor<R>() {

    init {
        require(!wrapped.isOver) {
            "Cannot create a ${MapperCursor::class.simpleName} out a cursor which is over"
        }
    }

    override val next: Cursor<out R>
        get() = wrapped.next.map(mapper)

    override val current: R? = wrapped.current?.let(mapper)

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false
}
