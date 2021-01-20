package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IntegerImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Integer : Numeric {

    override val isInt: Boolean
        get() = true

    override val value: BigInteger

    override val decimalValue: BigDecimal
        get() = BigDecimal.of(value)

    override val intValue: BigInteger
        get() = value

    override fun freshCopy(): Integer

    override fun freshCopy(scope: Scope): Integer

    companion object {

        @JvmField
        val INTEGER_REGEX_PATTERN =
            """^[+\-]?(0[xXbBoO])?[0-9A-Fa-f]+$""".toRegex()

        @JvmStatic
        @JsName("ofBigInteger")
        fun of(integer: BigInteger): Integer = IntegerImpl(integer)

        @JvmStatic
        @JsName("ofLong")
        fun of(integer: Long): Integer = of(BigInteger.of(integer))

        @JvmStatic
        @JsName("ofInt")
        fun of(integer: Int): Integer = of(BigInteger.of(integer))

        @JvmStatic
        @JsName("ofShort")
        fun of(integer: Short): Integer = of(BigInteger.of(integer.toLong()))

        @JvmStatic
        @JsName("ofByte")
        fun of(integer: Byte): Integer = of(BigInteger.of(integer.toLong()))

        @JvmStatic
        @JsName("parse")
        fun of(integer: String): Integer = of(BigInteger.of(integer))

        @JvmStatic
        @JsName("parseRadix")
        fun of(integer: String, radix: Int): Integer = of(BigInteger.of(integer, radix))

        @JvmField
        val ZERO = Integer.of(BigInteger.ZERO)

        @JvmField
        val ONE = Integer.of(BigInteger.ONE)

        @JvmField
        val MINUS_ONE = Integer.of(BigInteger.of(-1))
    }
}
