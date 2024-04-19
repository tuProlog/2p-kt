package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Integer : Numeric {
    override val isInteger: Boolean
        get() = true

    override val value: BigInteger

    override val decimalValue: BigDecimal
        get() = BigDecimal.of(value)

    override val intValue: BigInteger
        get() = value

    override fun freshCopy(): Integer

    override fun freshCopy(scope: Scope): Integer

    override fun asInteger(): Integer = this

    companion object {
        @JvmField
        val PATTERN = Terms.INTEGER_PATTERN

        @JvmStatic
        @JsName("ofBigInteger")
        fun of(value: BigInteger): Integer = TermFactory.default.intOf(value)

        @JvmStatic
        @JsName("ofLong")
        fun of(value: Long): Integer = TermFactory.default.intOf(value)

        @JvmStatic
        @JsName("ofInt")
        fun of(value: Int): Integer = TermFactory.default.intOf(value)

        @JvmStatic
        @JsName("ofShort")
        fun of(value: Short): Integer = TermFactory.default.intOf(value)

        @JvmStatic
        @JsName("ofByte")
        fun of(value: Byte): Integer = TermFactory.default.intOf(value)

        @JvmStatic
        @JsName("parse")
        fun of(value: String): Integer = TermFactory.default.intOf(value)

        @JvmStatic
        @JsName("parseRadix")
        fun of(
            value: String,
            radix: Int,
        ): Integer = TermFactory.default.intOf(value, radix)

        @JvmField
        val ZERO = Integer.of(BigInteger.ZERO)

        @JvmField
        val ONE = Integer.of(BigInteger.ONE)

        @JvmField
        val MINUS_ONE = Integer.of(BigInteger.of(-1))
    }
}
