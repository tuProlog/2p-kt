package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
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

    override fun freshCopy(): Numeric = this

    override fun freshCopy(scope: Scope): Numeric = this

    @JsName("compareValueTo")
    fun compareValueTo(other: Numeric): Int = decimalValue.compareTo(other.decimalValue)

    companion object {

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
        fun of(value: Number): Numeric = when (value) {
            // avoiding string format is necessary for "floats", to maintain full precision during conversions
            is Float -> of(value)
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
