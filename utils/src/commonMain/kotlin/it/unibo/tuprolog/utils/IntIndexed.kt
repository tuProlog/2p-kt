package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.IntIndexedImpl

interface IntIndexed<T> :
    Indexed<Int, T>,
    Comparable<IntIndexed<T>> {
    override fun compareTo(other: IntIndexed<T>): Int = index - other.index

    override fun <R> map(mapper: (T) -> R): IntIndexed<R>

    companion object {
        fun <T> of(
            index: Int,
            value: T,
        ): IntIndexed<T> = IntIndexedImpl(index, value)
    }
}
