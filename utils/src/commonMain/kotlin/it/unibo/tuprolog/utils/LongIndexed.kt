package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.LongIndexedImpl

/**
 * An [Indexed] value whose [index] is a [Long], comparable to other [LongIndexed] values by [index]. This is
 * the type produced by [Sequence.longIndexed], the 64-bit counterpart of [IntIndexed], useful whenever the
 * position of a value may exceed [Int.MAX_VALUE] (e.g. `it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause`
 * in the `:theory` module indexes clauses by insertion order using a [Long], since a knowledge base may grow
 * without a practical [Int]-sized bound).
 * @param T is the type of the indexed value
 */
interface LongIndexed<T> :
    Indexed<Long, T>,
    Comparable<LongIndexed<T>> {
    /** Compares two [LongIndexed] values by their [index], ignoring [value]. */
    override fun compareTo(other: LongIndexed<T>): Int = index.compareTo(other.index)

    /** Returns a new [LongIndexed] with the same [index], and [value] transformed via [mapper]. */
    override fun <R> map(mapper: (T) -> R): LongIndexed<R>

    companion object {
        /** Creates a new [LongIndexed] pairing [index] with [value]. */
        fun <T> of(
            index: Long,
            value: T,
        ): LongIndexed<T> = LongIndexedImpl(index, value)
    }
}
