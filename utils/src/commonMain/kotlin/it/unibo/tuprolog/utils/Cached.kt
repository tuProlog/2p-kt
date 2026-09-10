package it.unibo.tuprolog.utils

import it.unibo.tuprolog.utils.impl.CachedImpl

/**
 * A lazily-computed, invalidatable single value, i.e. a memoized version of a `() -> T` [generator][of].
 *
 * Unlike Kotlin's stdlib `lazy { ... }` delegate, a [Cached] value can be explicitly [invalidate]d and later
 * [regenerate]d on demand, which makes it suitable for values that are expensive to compute but occasionally
 * need to be recomputed (e.g. because some external state they depend on has changed). Use [Cached.of] to
 * wrap a generator function:
 * ```kotlin
 * val cached: Cached<T> = Cached.of { computeExpensiveValue() }
 * cached.value // computes and caches the value on first access
 * cached.invalidate() // forces the next access to recompute the value
 * ```
 * @param T is the type of the cached value
 */
interface Cached<T> {
    companion object {
        /**
         * Creates a new [Cached] value whose content is (re)computed by invoking [generator] whenever it is
         * accessed while [invalid][isInvalid].
         */
        fun <T> of(generator: () -> T): Cached<T> = CachedImpl(generator)
    }

    /**
     * Whether [value] currently holds an up-to-date, already-computed result (i.e. [generator] does not need
     * to be invoked again upon the next access).
     */
    val isValid: Boolean

    /**
     * Whether [value] needs to be (re)computed, by invoking [generator] again, upon the next access.
     * Always the logical negation of [isValid].
     */
    val isInvalid: Boolean

    /**
     * Retrieves the cached value, computing it first (via the generator passed to [Cached.of]) if it is
     * currently [isInvalid].
     */
    val value: T

    /**
     * Ensures [value] holds an up-to-date result, computing it via the generator passed to [Cached.of] if
     * it is currently [isInvalid]. Does nothing if the value is already [isValid].
     */
    fun regenerate()

    /**
     * Ensures the cached value is up-to-date (see [regenerate]), then applies [consumer] to it, returning
     * the result of [consumer].
     */
    fun <R> regenerating(consumer: (T) -> R): R {
        regenerate()
        return value.let(consumer)
    }

    /**
     * Marks the cached value as [isInvalid], so that it is recomputed upon the next access to [value].
     */
    fun invalidate()

    /**
     * Applies [consumer] to the cached value and returns the result wrapped in [Optional.Some], but only if
     * the value is currently [isValid]; otherwise, returns [Optional.None] without forcing a (re)computation.
     */
    fun <R> ifValid(consumer: (T) -> R): Optional<out R> =
        if (isValid) {
            Optional.of(value.let(consumer))
        } else {
            Optional.none()
        }
}
