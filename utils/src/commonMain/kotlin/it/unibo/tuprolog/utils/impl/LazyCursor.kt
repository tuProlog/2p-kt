package it.unibo.tuprolog.utils.impl

internal data class LazyCursor<T>(
    val delegate: Iterator<T>,
) : AbstractCursor<T>() {
    // read through `.value`, not `by`: on JS, delegation builds a property reference at every read
    private val lazyWrapped =
        lazy {
            NonLastCursor(delegate)
        }

    private val wrapped: AbstractCursor<T>
        get() = lazyWrapped.value

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
