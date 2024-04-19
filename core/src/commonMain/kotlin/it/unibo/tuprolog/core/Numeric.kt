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
        fun of(value: BigDecimal): Real = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("ofDouble")
        fun of(value: Double): Real = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("ofFloat")
        fun of(value: Float): Real = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("ofBigInteger")
        fun of(value: BigInteger): Integer = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("ofInteger")
        fun of(value: Int): Integer = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("ofLong")
        fun of(value: Long): Integer = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("ofShort")
        fun of(value: Short): Integer = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("ofByte")
        fun of(value: Byte): Integer = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("of")
        fun of(value: Number): Numeric = TermFactory.default.numOf(value)

        @JvmStatic
        @JsName("parse")
        fun of(value: String): Numeric = TermFactory.default.numOf(value)
    }
}
