package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IntegralImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

interface Integral : Numeric {

    override val isInt: Boolean
        get() = true

    val value: BigInteger

    override val decimalValue: BigDecimal
        get() = BigDecimal.of(value)

    override val intValue: BigInteger
        get() = value

    override fun freshCopy(): Integral = this

    override fun freshCopy(scope: Scope): Integral = this

    companion object {
        val INTEGRAL_REGEX_PATTERN = """^[+\-]?(0[xXbBoO])?[0-9A-Fa-f]+$""".toRegex()

        fun of(integer: BigInteger): Integral = IntegralImpl(integer)
        fun of(integer: Long): Integral = of(BigInteger.of(integer))
        fun of(integer: Int): Integral = of(BigInteger.of(integer))
        fun of(integer: Short): Integral = of(BigInteger.of(integer.toLong()))
        fun of(integer: Byte): Integral = of(BigInteger.of(integer.toLong()))
        fun of(integer: String): Integral = of(BigInteger.of(integer))
        fun of(integer: String, radix: Int): Integral = of(BigInteger.of(integer, radix))
    }
}
