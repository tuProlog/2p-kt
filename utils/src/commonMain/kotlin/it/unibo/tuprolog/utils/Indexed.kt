package it.unibo.tuprolog.utils

/**
 * A [value] of type [T] paired with an [index] of type [K], i.e. a labelled/positional value.
 *
 * This is a generalization of the (index, value) pairs produced by, e.g., [kotlin.collections.withIndex],
 * generic over the type of the index; see [IntIndexed] and [LongIndexed] for the concrete flavours used
 * throughout the codebase (e.g. by [Sequence.indexed] and [Sequence.longIndexed]), and destructure it as a
 * pair via [component1]/[component2]:
 * ```kotlin
 * for ((index, value) in sequence.indexed()) { ... }
 * ```
 * @param K is the type of the index
 * @param T is the type of the indexed value
 */
interface Indexed<K, T> {
    /** The index/position associated with [value]. */
    val index: K

    /** The indexed value. */
    val value: T

    /** Destructuring component corresponding to [index]. */
    operator fun component1(): K = index

    /** Destructuring component corresponding to [value]. */
    operator fun component2(): T = value

    /** Returns a new [Indexed] with the same [index], and [value] transformed via [mapper]. */
    fun <R> map(mapper: (T) -> R): Indexed<K, R>
}
