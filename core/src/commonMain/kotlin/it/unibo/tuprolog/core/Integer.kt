package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IntegerImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * A [Numeric] whose [value] is an arbitrary-precision integer, e.g. `1`, `-42`, or a value far outside the
 * range of a [Long]. Being arbitrary-precision (backed by [BigInteger]) means arithmetic on [Integer]s never
 * silently overflows, matching how ISO Prolog integers are meant to behave.
 */
interface Integer : Numeric {
    override val isInteger: Boolean
        get() = true

    /** The exact value of this integer. */
    override val value: BigInteger

    override val decimalValue: BigDecimal
        get() = BigDecimal.of(value)

    override val intValue: BigInteger
        get() = value

    override fun freshCopy(): Integer

    override fun freshCopy(scope: Scope): Integer

    override fun asInteger(): Integer = this

    companion object {
        /** The pattern recognizing the textual representation of an [Integer]. See [Struct] for parsing helpers. */
        @JvmField
        val PATTERN = Terms.INTEGER_PATTERN

        /** Creates an [Integer] with the given [integer] value. */
        @JvmStatic
        @JsName("ofBigInteger")
        fun of(integer: BigInteger): Integer = IntegerImpl(integer)

        /** Creates an [Integer] with the given [integer] value. */
        @JvmStatic
        @JsName("ofLong")
        fun of(integer: Long): Integer = of(BigInteger.of(integer))

        /** Creates an [Integer] with the given [integer] value. */
        @JvmStatic
        @JsName("ofInt")
        fun of(integer: Int): Integer = of(BigInteger.of(integer))

        /** Creates an [Integer] with the given [integer] value. */
        @JvmStatic
        @JsName("ofShort")
        fun of(integer: Short): Integer = of(BigInteger.of(integer.toLong()))

        /** Creates an [Integer] with the given [integer] value. */
        @JvmStatic
        @JsName("ofByte")
        fun of(integer: Byte): Integer = of(BigInteger.of(integer.toLong()))

        /**
         * Parses [integer] (in base 10) into an [Integer].
         * @throws NumberFormatException if [integer] is not a valid base-10 integer literal
         */
        @JvmStatic
        @JsName("parse")
        fun of(integer: String): Integer = of(BigInteger.of(integer))

        /**
         * Parses [integer], expressed in the given [radix], into an [Integer].
         * @throws NumberFormatException if [integer] is not a valid integer literal in [radix]
         */
        @JvmStatic
        @JsName("parseRadix")
        fun of(
            integer: String,
            radix: Int,
        ): Integer = of(BigInteger.of(integer, radix))

        /** The [Integer] `0`. */
        @JvmField
        val ZERO = Integer.of(BigInteger.ZERO)

        /** The [Integer] `1`. */
        @JvmField
        val ONE = Integer.of(BigInteger.ONE)

        /** The [Integer] `-1`. */
        @JvmField
        val MINUS_ONE = Integer.of(BigInteger.of(-1))
    }
}
