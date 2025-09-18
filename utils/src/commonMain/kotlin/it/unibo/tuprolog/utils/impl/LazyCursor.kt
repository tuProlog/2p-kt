package it.unibo.tuprolog.utils.impl

internal data class LazyCursor<T>(
    val delegate: Iterator<T>,
) : AbstractCursor<T>() {
    private val wrapped: AbstractCursor<T> by lazy {
        NonLastCursor(delegate)
    }

    override val isLazy: Boolean
        get() = true

    override val next: AbstractCursor<out T>
        get() = wrapped.next

    override val current: T?
        get() = wrapped.current

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String = super<AbstractCursor>.toString()

    override fun iterator(): Iterator<T> = wrapped.iterator()
}
