package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RealImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

/**
 * A [Numeric] whose [value] is an arbitrary-precision decimal number, e.g. `1.0`, `-3.14`. Being backed by
 * [BigDecimal] rather than a [Double] avoids the rounding surprises of binary floating point, at the cost of
 * requiring explicit conversion when interoperating with APIs that expect a native floating-point type.
 */
interface Real : Numeric {
    override val isReal: Boolean
        get() = true

    /** The exact value of this real number. */
    override val value: BigDecimal

    override val decimalValue: BigDecimal
        get() = value

    override val intValue: BigInteger
        get() = value.toBigInteger()

    override fun freshCopy(): Real

    override fun freshCopy(scope: Scope): Real

    override fun asReal(): Real = this

    companion object {
        /** The pattern recognizing the textual representation of a [Real]. */
        @JvmField
        val PATTERN = Terms.REAL_PATTERN

        /**
         * Renders [real] ensuring the result contains a decimal point, appending `.0` if [real]'s own
         * [toString] would otherwise look like an integer (e.g. `1` becomes `1.0`).
         */
        @JvmStatic
        @JsName("toStringEnsuringDecimal")
        fun toStringEnsuringDecimal(real: BigDecimal): String =
            real.toString().let {
                if ("." !in it) "$it.0" else it
            }

        /** Creates a [Real] with the given [real] value. */
        @JvmStatic
        @JsName("ofBigDecimal")
        fun of(real: BigDecimal): Real = RealImpl(real)

        /** Creates a [Real] with the given [real] value. */
        @JvmStatic
        @JsName("ofDouble")
        fun of(real: Double): Real = of(BigDecimal.of(real))

        /** Creates a [Real] with the given [real] value. */
        @JvmStatic
        @JsName("ofFloat")
        fun of(real: Float): Real = of(BigDecimal.of(real))

        /**
         * Parses [real] into a [Real].
         * @throws NumberFormatException if [real] is not a valid decimal literal
         */
        @JvmStatic
        @JsName("parse")
        fun of(real: String): Real = of(BigDecimal.of(real))

        /** The [Real] `0.0`. */
        @JvmField
        val ZERO = Real.of(BigDecimal.ZERO)

        /** The [Real] `1.0`. */
        @JvmField
        val ONE = Real.of(BigDecimal.ONE)

        /** The [Real] `-1.0`. */
        @JvmField
        val MINUS_ONE = Real.of(-BigDecimal.ONE)

        /** The [Real] `0.5`. */
        @JvmField
        val ONE_HALF = Real.of(BigDecimal.ONE_HALF)

        /** The [Real] `0.1`. */
        @JvmField
        val ONE_TENTH = Real.of(BigDecimal.ONE_TENTH)
    }
}
