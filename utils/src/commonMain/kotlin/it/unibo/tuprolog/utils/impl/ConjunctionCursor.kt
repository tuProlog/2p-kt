package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Cursor

internal class ConjunctionCursor<T>(
    val first: Cursor<out T>,
    val second: AbstractCursor<out T>,
) : AbstractCursor<T>() {
    override val isOver: Boolean
        get() = first.isOver && second.isOver

    // read through `.value`, not `by`: on JS, delegation builds a property reference at every read
    private val lazyNext =
        lazy {
            when {
                first.hasNext -> ConjunctionCursor(first.next, second)
                second.isLazy -> second.next
                else -> second
            }
        }

    override val next: AbstractCursor<out T>
        get() = lazyNext.value

    override val current: T?
        get() = if (first.hasNext) first.current else second.current

    override val hasNext: Boolean
        get() = first.hasNext || second.hasNext

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ConjunctionCursor<*>

        if (first != other.first) return false
        if (second != other.second) return false

        return true
    }

    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        return result
    }
}
