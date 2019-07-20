package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IntegerImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

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

    override fun <T> accept(visitor: TermVisitor<T>): T =
            visitor.visit(this)

    companion object {
        val INTEGER_REGEX_PATTERN = """^[+\-]?(0[xXbBoO])?[0-9A-Fa-f]+$""".toRegex()

        fun of(integer: BigInteger): Integer = IntegerImpl(integer)
        fun of(integer: Long): Integer = of(BigInteger.of(integer))
        fun of(integer: Int): Integer = of(BigInteger.of(integer))
        fun of(integer: Short): Integer = of(BigInteger.of(integer.toLong()))
        fun of(integer: Byte): Integer = of(BigInteger.of(integer.toLong()))
        fun of(integer: String): Integer = of(BigInteger.of(integer))
        fun of(integer: String, radix: Int): Integer = of(BigInteger.of(integer, radix))
    }
}
