package it.unibo.tuprolog.utils

data class Indexed<T>(
    val index: Long,
    val value: T
) : Comparable<Indexed<T>> {
    override fun compareTo(other: Indexed<T>): Int {
        return (index - other.index).toInt()
    }

    fun <R> map(mapper: (T) -> R): Indexed<R> {
        return Indexed(index, mapper(value))
    }
}
