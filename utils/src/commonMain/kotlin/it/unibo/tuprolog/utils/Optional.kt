package it.unibo.tuprolog.utils

/**
 * A container that either holds exactly one value of type [T] ([Some]), or holds none ([None]), used across
 * the codebase as an explicit, `null`-safe substitute for a nullable `T?` wherever `null` itself could be
 * a meaningful value to store (so that "no value" and "the value happens to be `null`" cannot be confused),
 * e.g. as the return type of [Cache.get]/[Cache.set] (the evicted pair, if any) or [Cached.ifValid].
 *
 * Being a `sealed class`, it can be exhaustively pattern-matched with a `when`:
 * ```kotlin
 * when (val cached = mguCache[mguRequest]) {
 *     is Optional.Some -> cached.value
 *     else -> { /* compute and cache the value */ }
 * }
 * ```
 * Use [Optional.of] to wrap a nullable value (mapping `null` to [None]), [Optional.some] to wrap a
 * known-non-null value, and [Optional.none] to get the empty instance.
 * @param T is the type of the contained value, if any
 */
sealed class Optional<T> {
    companion object {
        /** Wraps [value] into a [Some]. */
        fun <T> some(value: T): Some<out T> = Some(value)

        /** Wraps [value] into a [Some], or returns [none] if [value] is `null`. */
        fun <T> of(value: T?): Optional<out T> =
            if (value == null) {
                none()
            } else {
                some(value)
            }

        /** Returns the (single, shared) empty [Optional] instance, i.e. [None]. */
        fun <T> none(): Optional<out T> = None
    }

    /** The case of [Optional] holding exactly one, non-`null` [value]. */
    data class Some<T>(
        override val value: T,
    ) : Optional<T>() {
        override val isPresent: Boolean
            get() = true

        /** Applies [function] to [value], returning a [Some] holding the result. */
        override fun <R> map(function: (T) -> R): Some<R> = Some(function(value))

        /** Returns this instance if [value] satisfies [predicate], or [None] otherwise. */
        override fun filter(predicate: (T) -> Boolean): Optional<out T> =
            when {
                predicate(value) -> this
                else -> None
            }

        /** Returns a single-element [Sequence] containing [value]. */
        override fun toSequence(): Sequence<T> = sequenceOf(value)

        override fun toString(): String = "Some($value)"
    }

    /** The case of [Optional] holding no value. Always the same, shared singleton instance. */
    object None : Optional<Nothing>() {
        override val value: Nothing?
            get() = null

        override val isPresent: Boolean
            get() = false

        /** Returns [None], without invoking [function] (there is no value to apply it to). */
        override fun <R> map(function: (Nothing) -> R): None = None

        /** Returns [None], without invoking [predicate] (there is no value to test). */
        override fun filter(predicate: (Nothing) -> Boolean): None = None

        /** Returns an empty [Sequence]. */
        override fun toSequence(): Sequence<Nothing> = emptySequence()

        override fun toString(): String = "None"
    }

    /** The contained value, or `null` if this is [None]. */
    abstract val value: T?

    /** Whether this [Optional] holds a value, i.e. whether it is a [Some]. */
    abstract val isPresent: Boolean

    /** Whether this [Optional] holds no value, i.e. whether it is [None]. Always `!isPresent`. */
    val isAbsent: Boolean
        get() = !isPresent

    /** Transforms the contained value, if any, via [function]; propagates [None] unchanged otherwise. */
    abstract fun <R> map(function: (T) -> R): Optional<out R>

    /** Keeps the contained value, if any, only if it satisfies [predicate]; propagates [None] unchanged. */
    abstract fun filter(predicate: (T) -> Boolean): Optional<out T>

    /** Converts this [Optional] into a [Sequence] of zero or one elements. */
    abstract fun toSequence(): Sequence<T>

    abstract override fun toString(): String
}
