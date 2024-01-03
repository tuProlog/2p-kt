package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Numeric : Constant {
    override val isNumber: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = emptySequence()

    @JsName("decimalValue")
    val decimalValue: BigDecimal

    @JsName("intValue")
    val intValue: BigInteger

    override fun freshCopy(): Numeric

    override fun freshCopy(scope: Scope): Numeric

    @JsName("compareValueTo")
    fun compareValueTo(other: Numeric): Int = decimalValue.compareTo(other.decimalValue)

    override fun asNumeric(): Numeric = this

    companion object {
        @JvmField
        val INTEGER_PATTERN = Terms.INTEGER_PATTERN

        @JvmField
        val REAL_PATTERN = Terms.REAL_PATTERN

        @JvmStatic
        @JsName("ofBigDecimal")
        fun of(decimal: BigDecimal): Real = Real.of(decimal)

        @JvmStatic
        @JsName("ofDouble")
        fun of(decimal: Double): Real = Real.of(decimal)

        @JvmStatic
        @JsName("ofFloat")
        fun of(decimal: Float): Real = Real.of(decimal)

        @JvmStatic
        @JsName("ofBigInteger")
        fun of(integer: BigInteger): Integer = Integer.of(integer)

        @JvmStatic
        @JsName("ofInteger")
        fun of(integer: Int): Integer = Integer.of(integer)

        @JvmStatic
        @JsName("ofLong")
        fun of(integer: Long): Integer = Integer.of(integer)

        @JvmStatic
        @JsName("ofShort")
        fun of(integer: Short): Integer = Integer.of(integer)

        @JvmStatic
        @JsName("ofByte")
        fun of(integer: Byte): Integer = Integer.of(integer)

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
