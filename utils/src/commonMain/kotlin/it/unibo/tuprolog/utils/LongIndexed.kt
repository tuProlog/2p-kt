package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.LongIndexedImpl

interface LongIndexed<T> :
    Indexed<Long, T>,
    Comparable<LongIndexed<T>> {
    override fun compareTo(other: LongIndexed<T>): Int = (index - other.index).toInt()

    override fun <R> map(mapper: (T) -> R): LongIndexed<R>

    companion object {
        fun <T> of(
            index: Long,
            value: T,
        ): LongIndexed<T> = LongIndexedImpl(index, value)
    }
}
