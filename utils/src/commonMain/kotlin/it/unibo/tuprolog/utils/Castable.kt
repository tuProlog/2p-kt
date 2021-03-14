package it.unibo.tuprolog.utils

/**
 * A type for all object which can be down-casted via explicit methods
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
    fun <U : T> `as`(): U? = this as? U
}
