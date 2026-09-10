package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * Base type for numeric [Constant]s, i.e. Prolog numbers.
 *
 * [Numeric] splits into two disjoint sub-types, [Integer] and [Real], because Prolog itself distinguishes
 * integers from floating-point numbers both syntactically (`1` vs `1.0`) and semantically (arithmetic
 * built-ins like `//` vs `/` behave differently depending on the operand types, and ISO term ordering treats
 * "value-equal" integers and reals as distinct terms unless [structurallyEquals][Term.structurallyEquals] is
 * used). Client code that only cares about "some number" can program against [Numeric] and use [decimalValue]
 * / [intValue] / [compareValueTo] to compare or convert regardless of which concrete sub-type is involved;
 * code that must preserve or check Prolog's integer/float distinction should use [Integer] or [Real] directly.
 *
 * New [Numeric] instances are best created through the factory methods in this companion (which pick the
 * most appropriate sub-type automatically), or directly via [Integer.of] / [Real.of] when the desired
 * sub-type is already known.
 */
interface Numeric : Constant {
    override val isNumber: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = emptySequence()

    /** The value of this number, widened to a [BigDecimal]. Always exact for [Integer]s. */
    @JsName("decimalValue")
    val decimalValue: BigDecimal

    /** The value of this number, narrowed to a [BigInteger]. For [Real]s, this truncates the fractional part. */
    @JsName("intValue")
    val intValue: BigInteger

    override fun freshCopy(): Numeric

    override fun freshCopy(scope: Scope): Numeric

    /**
     * Compares this number to [other] by numeric value alone (via [decimalValue]), regardless of whether
     * either is an [Integer] or a [Real]. This is different from [Term.compareTo], which orders integers
     * before reals of the same the term-comparison sense rather than by value.
     */
    @JsName("compareValueTo")
    fun compareValueTo(other: Numeric): Int = decimalValue.compareTo(other.decimalValue)

    override fun asNumeric(): Numeric = this

    companion object {
        /** The pattern recognizing textual representations of [Integer]s. See [Integer.of]. */
        @JvmField
        val INTEGER_PATTERN = Terms.INTEGER_PATTERN

        /** The pattern recognizing textual representations of [Real]s. See [Real.of]. */
        @JvmField
        val REAL_PATTERN = Terms.REAL_PATTERN

        /** Creates a [Real] out of [decimal]. See [Real.of]. */
        @JvmStatic
        @JsName("ofBigDecimal")
        fun of(decimal: BigDecimal): Real = Real.of(decimal)

        /** Creates a [Real] out of [decimal]. See [Real.of]. */
        @JvmStatic
        @JsName("ofDouble")
        fun of(decimal: Double): Real = Real.of(decimal)

        /** Creates a [Real] out of [decimal]. See [Real.of]. */
        @JvmStatic
        @JsName("ofFloat")
        fun of(decimal: Float): Real = Real.of(decimal)

        /** Creates an [Integer] out of [integer]. See [Integer.of]. */
        @JvmStatic
        @JsName("ofBigInteger")
        fun of(integer: BigInteger): Integer = Integer.of(integer)

        /** Creates an [Integer] out of [integer]. See [Integer.of]. */
        @JvmStatic
        @JsName("ofInteger")
        fun of(integer: Int): Integer = Integer.of(integer)

        /** Creates an [Integer] out of [integer]. See [Integer.of]. */
        @JvmStatic
        @JsName("ofLong")
        fun of(integer: Long): Integer = Integer.of(integer)

        /** Creates an [Integer] out of [integer]. See [Integer.of]. */
        @JvmStatic
        @JsName("ofShort")
        fun of(integer: Short): Integer = Integer.of(integer)

        /** Creates an [Integer] out of [integer]. See [Integer.of]. */
        @JvmStatic
        @JsName("ofByte")
        fun of(integer: Byte): Integer = Integer.of(integer)

        /**
         * Creates a [Numeric] out of the given [value], picking [Integer] or [Real] depending on its
         * Kotlin type ([Double]/[Float] become [Real], integral types become [Integer]; any other
         * [Number] is converted via its [Any.toString] representation, then parsed like [of] would).
         */
        @JvmStatic
        @JsName("of")
        fun of(value: Number): Numeric =
            when (value) {
                // avoiding string format is necessary for "floats", to maintain full precision during conversions
                is Double -> of(value)
                is Int -> of(value)
                is Float -> of(value)
                is Long -> of(value)
                is Short -> of(value)
                is Byte -> of(value)
                else -> of(value.toString())
            }

        /**
         * Parses [number] into a [Numeric], trying [Integer.of] first and falling back to [Real.of] if that fails.
         * @throws NumberFormatException if [number] can be parsed as neither an [Integer] nor a [Real]
         */
        @JvmStatic
        @JsName("parse")
        fun of(number: String): Numeric =
            try {
                Integer.of(number)
            } catch (ex: NumberFormatException) {
                Real.of(number)
            }
    }
}
