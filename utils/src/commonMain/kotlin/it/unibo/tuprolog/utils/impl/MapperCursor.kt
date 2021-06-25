package it.unibo.tuprolog.utils.impl

internal class MapperCursor<T, R>(
    private val wrapped: AbstractCursor<out T>,
    private val mapper: (T) -> R
) : AbstractCursor<R>() {

    override val next: AbstractCursor<out R>
        get() = wrapped.next.map(mapper)

    override val current: R by lazy { mapper(wrapped.current!!) }

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

    override fun <X> map(mapper: (R) -> X): AbstractCursor<out X> = ValueCursor(mapper(current), next.map(mapper))
}
