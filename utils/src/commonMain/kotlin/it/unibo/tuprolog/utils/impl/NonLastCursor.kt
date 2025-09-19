package it.unibo.tuprolog.utils.impl

internal class NonLastCursor<T>(
    source: Iterator<T>,
) : AbstractCursor<T>() {
    private var source: Iterator<T>? = source

    override val next: AbstractCursor<out T> by lazy {
        AbstractCursor.of(source).also { this.source = null }
    }

    override val current: T = source.next()

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String = super<AbstractCursor>.toString()
}
