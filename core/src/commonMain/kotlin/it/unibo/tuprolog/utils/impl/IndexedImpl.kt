package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.Indexed

internal data class IndexedImpl<T>(
    override val index: Long,
    override val value: T
) : Indexed<T> {
    override fun compareTo(other: Indexed<T>): Int {
        return (index - other.index).toInt()
    }

    override fun <R> map(mapper: (T) -> R): Indexed<R> {
        return IndexedImpl(index, mapper(value))
    }
}