package it.unibo.tuprolog.utils.impl

import it.unibo.tuprolog.utils.LongIndexed

internal data class LongIndexedImpl<T>(
    override val index: Long,
    override val value: T
) : LongIndexed<T> {
    override fun compareTo(other: LongIndexed<T>): Int {
        return (index - other.index).toInt()
    }

    override fun <R> map(mapper: (T) -> R): LongIndexed<R> {
        return LongIndexedImpl(index, mapper(value))
    }
}
