package it.unibo.tuprolog.utils

/**
 * A type for all objects belonging to a hierarchy rooted in [T] which can be down-casted to any subtype of
 * [T] via explicit methods, rather than via Kotlin's `as`/`as?` operators.
 *
 * This is useful for hierarchies (e.g. `it.unibo.tuprolog.core.Term` in the `:core` module) exposing many
 * mutually exclusive subtypes, where call sites need a concise, fluent way to narrow a value down after a
 * type check (e.g. `term.castToInteger()`), without repeating verbose `as Integer` casts everywhere. Callers
 * that want a `null` instead of an exception on a failed cast should use [as] rather than [castTo]:
 * ```kotlin
 * sealed interface Term : ... , Castable<Term> {
 *     fun castToInteger(): Integer = castTo()
 * }
 * ```
 * @param T is the root of the hierarchy of castable types
 */
@Suppress("UNCHECKED_CAST")
interface Castable<T : Castable<T>> {
    /**
     * Down-casts the current object to [U], if possible
     * @throws ClassCastException if the current object is not an instance of [U]
     * @return the current object, casted to [U]
     */
    fun <U : T> castTo(): U = this as U

    /**
     * Casts the current object to [U], if possible, or returns `null` otherwise
     * @return the current object, casted to [U], or `null`, if the current object is not an instance of [U]
     */
    @Suppress("ktlint:standard:function-naming")
    fun <U : T> `as`(): U? = this as? U
}
