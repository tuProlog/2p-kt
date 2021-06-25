package it.unibo.tuprolog.utils.impl

internal class MapperCursor<T, R> : AbstractCursor<R> {

    constructor(wrapped: AbstractCursor<out T>, mapper: (T) -> R) {
        this.wrapped = wrapped
        this.mapper = mapper
        this.current = wrapped.current?.let(mapper)
    }

    constructor(wrapped: AbstractCursor<out T>, mapper: (T) -> R, current: R?) {
        this.wrapped = wrapped
        this.mapper = mapper
        this.current = current
    }

    private val wrapped: AbstractCursor<out T>

    private val mapper: (T) -> R

    override val current: R?

    private var hasNextValue: Boolean = false

    private var nextValue: R? = null

    override val next: AbstractCursor<out R>
        get() = if (hasNextValue) {
            wrapped.next.quickMap(nextValue, mapper)
        } else {
            wrapped.next.map(mapper).also {
                hasNextValue = true
                nextValue = it.current
            }
        }

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MapperCursor<*, *>

        if (wrapped != other.wrapped) return false
        if (mapper != other.mapper) return false

        return true
    }

    override fun hashCode(): Int {
        var result = wrapped.hashCode()
        result = 31 * result + mapper.hashCode()
        return result
    }
}
