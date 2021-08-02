package it.unibo.tuprolog.utils.impl

internal class ValueCursor<T>(
    override val current: T,
    override val next: AbstractCursor<out T>
) : AbstractCursor<T>() {
    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false
}
