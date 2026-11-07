package it.unibo.tuprolog.utils.impl

internal class NonLastCursor<T>(
    source: Iterator<T>,
) : AbstractCursor<T>() {
    private var source: Iterator<T>? = source

    // read through `.value`, not `by`: on JS, delegation builds a property reference at every read
    private val lazyNext =
        lazy {
            AbstractCursor.of(source).also { this.source = null }
        }

    override val next: AbstractCursor<out T>
        get() = lazyNext.value

    override val current: T = source.next()

    override val hasNext: Boolean
        get() = true

    override val isOver: Boolean
        get() = false

    override fun toString(): String = super<AbstractCursor>.toString()
}
