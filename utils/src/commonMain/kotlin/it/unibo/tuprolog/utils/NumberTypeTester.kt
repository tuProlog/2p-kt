package it.unibo.tuprolog.utils

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

/**
 * Classifies platform [Number]s (e.g. `Int`, `Double`, `Float`, or any other JVM/JS numeric boxed type
 * encountered while converting external, non-Prolog values into Prolog terms) as integral or decimal, and
 * converts them into the arbitrary-precision [BigInteger]/[BigDecimal] types used internally to represent
 * Prolog numbers, based on their [String] representation rather than on their runtime class.
 *
 * Classification looks at [Number.toString] (memoized per-instance in an internal [Cache], since the same
 * number is typically inspected more than once when it is turned into a term) instead of switching on the
 * concrete [Number] subtype, which keeps this tester agnostic to whatever numeric types a given host
 * platform (JVM, JS, ...) or interop layer (e.g. Java/Python object bridging in the `oop-lib`/`dsl-core`
 * modules) happens to expose. Since [isInteger], [toInteger] and [toDecimal] are declared as extensions on
 * [Number], they must be used from a receiver scope, typically via [with]:
 * ```kotlin
 * private val numberTypeTester = NumberTypeTester()
 * // ...
 * with(numberTypeTester) {
 *     when {
 *         source.isInteger -> Integer.of(source.toInteger())
 *         else -> Real.of(source.toDecimal())
 *     }
 * }
 * ```
 * The [numberIsInteger], [numberToInteger] and [numberToDecimal] methods below offer the very same
 * behaviour as plain (non-extension) methods, for callers (e.g. from Java, or other languages with no
 * extension-function syntax) that cannot conveniently open a [with] block.
 */
class NumberTypeTester {
    companion object {
        private val INT_REGEX = "[0-9]+".toRegex()
    }

    private val numberCache: Cache<Any, String> = Cache.simpleLru(8)

    /**
     * Whether this number's [String] representation (as per [Number.toString]) consists exclusively of
     * decimal digits, i.e. it has no sign, decimal point, or exponent part.
     */
    val Number.isInteger: Boolean
        get() {
            return INT_REGEX.matches(stringRepresentation)
        }

    private val Number.stringRepresentation
        get() = numberCache.getOrSet(this) { toString() }

    /**
     * Converts this number into an arbitrary-precision [BigInteger], by parsing its [String] representation.
     * Should only be called when [isInteger] is `true`.
     * @throws NumberFormatException if this number's [String] representation is not a valid integer literal
     */
    fun Number.toInteger(): BigInteger = BigInteger.of(stringRepresentation)

    /**
     * Converts this number into an arbitrary-precision [BigDecimal], by parsing its [String] representation.
     * @throws NumberFormatException if this number's [String] representation is not a valid decimal literal
     */
    fun Number.toDecimal(): BigDecimal = BigDecimal.of(stringRepresentation)

    /**
     * Non-extension equivalent of [Number.isInteger], for callers that cannot use a [with] receiver scope.
     */
    fun numberIsInteger(number: Number): Boolean = number.isInteger

    /**
     * Non-extension equivalent of [Number.toInteger], for callers that cannot use a [with] receiver scope.
     * @throws NumberFormatException if [number]'s [String] representation is not a valid integer literal
     */
    fun numberToInteger(number: Number): BigInteger = number.toInteger()

    /**
     * Non-extension equivalent of [Number.toDecimal], for callers that cannot use a [with] receiver scope.
     * @throws NumberFormatException if [number]'s [String] representation is not a valid decimal literal
     */
    fun numberToDecimal(number: Number): BigDecimal = number.toDecimal()
}
