package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.IndexedImpl

interface Indexed<T> : Comparable<Indexed<T>> {
    val index: Long

    val value: T

    override fun compareTo(other: Indexed<T>): Int {
        return (index - other.index).toInt()
    }

    fun <R> map(mapper: (T) -> R): Indexed<R>

    companion object {
        fun <T> of(index: Long, value: T): Indexed<T> {
            return IndexedImpl(index, value)
        }
    }
}

