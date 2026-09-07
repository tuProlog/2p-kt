package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.IntIndexedImpl

/**
 * An [Indexed] value whose [index] is an [Int], comparable to other [IntIndexed] values by [index]. This is
 * the type produced by [Sequence.indexed], the "counted" counterpart of [kotlin.collections.withIndex] used
 * throughout the codebase whenever a position needs to travel along with a value through further
 * transformations (e.g. `.map { it.map(...) }`), which a plain `IndexedValue`/destructured pair would not
 * allow as conveniently.
 * @param T is the type of the indexed value
 */
interface IntIndexed<T> :
    Indexed<Int, T>,
    Comparable<IntIndexed<T>> {
    /** Compares two [IntIndexed] values by their [index], ignoring [value]. */
    override fun compareTo(other: IntIndexed<T>): Int = index - other.index

    /** Returns a new [IntIndexed] with the same [index], and [value] transformed via [mapper]. */
    override fun <R> map(mapper: (T) -> R): IntIndexed<R>

    companion object {
        /** Creates a new [IntIndexed] pairing [index] with [value]. */
        fun <T> of(
            index: Int,
            value: T,
        ): IntIndexed<T> = IntIndexedImpl(index, value)
    }
}
