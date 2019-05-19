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

        private fun String.getRadix(): Pair<Int, String> {
            return when {
                this.contains("0B", ignoreCase = true) -> {
                    Pair(2, this.replaceFirst("0B", "").replaceFirst("0b", ""))
                }
                this.contains("0O", ignoreCase = true) -> {
                    Pair(8, this.replaceFirst("0O", "").replaceFirst("0o", ""))
                }
                this.contains("0X", ignoreCase = true) -> {
                    Pair(16, this.replaceFirst("0X", "").replaceFirst("0x", ""))
                }
                else -> Pair(10, this)
            }
        }

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
            val trimmed = integer.trim()
            val radixed = trimmed.getRadix()
            return Companion.of(radixed.second, radix = radixed.first)
        }

        fun of(integer: String, radix: Int): Integral {
            return Integral.of(BigInteger.of(integer, radix))
        }
    }
}
