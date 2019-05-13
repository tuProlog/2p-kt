package it.unibo.tuprolog.core

import io.github.gciatto.kt.math.BigDecimal
import io.github.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.impl.IntegralImpl

interface Integral : Numeric {

    override val isInt: Boolean
        get() = true

    val value: BigInteger

    override val decimalValue: BigDecimal
        get() = BigDecimal.of(value)

    override val intValue: BigInteger
        get() = value

    companion object {

        fun of(integer: BigInteger): Integral {
            return IntegralImpl(integer)
        }

        fun of(integer: Long): Integral {
            return Integral.of(BigInteger.of(integer))
        }

        fun of(integer: Int): Integral {
            return Integral.of(BigInteger.of(integer))
        }

        fun of(integer: Short): Integral {
            return Integral.of(BigInteger.of(integer.toLong()))
        }

        fun of(integer: Byte): Integral {
            return Integral.of(BigInteger.of(integer.toLong()))
        }

        fun of(integer: String): Integral {
            return Integral.of(BigInteger.of(integer))
        }

        fun of(integer: String, radix: Int): Integral {
            return Integral.of(BigInteger.of(integer, radix))
        }
    }
}
