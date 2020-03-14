package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IntegerImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
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

    override fun freshCopy(): Integer = this

    override fun freshCopy(scope: Scope): Integer = this

    companion object {

        @JvmField
        val INTEGER_REGEX_PATTERN = """^[+\-]?(0[xXbBoO])?[0-9A-Fa-f]+$""".toRegex()

        @JvmStatic
        fun of(integer: BigInteger): Integer = IntegerImpl(integer)

        @JvmStatic
        fun of(integer: Long): Integer = of(BigInteger.of(integer))

        @JvmStatic
        fun of(integer: Int): Integer = of(BigInteger.of(integer))

        @JvmStatic
        fun of(integer: Short): Integer = of(BigInteger.of(integer.toLong()))

        @JvmStatic
        fun of(integer: Byte): Integer = of(BigInteger.of(integer.toLong()))

        @JvmStatic
        fun of(integer: String): Integer = of(BigInteger.of(integer))

        @JvmStatic
        fun of(integer: String, radix: Int): Integer = of(BigInteger.of(integer, radix))
    }
}
