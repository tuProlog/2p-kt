package it.unibo.tuprolog.core

import io.github.gciatto.kt.math.BigDecimal
import io.github.gciatto.kt.math.BigInteger

interface Numeric : Term {

    override val isNumber: Boolean
        get() = true

    override val isGround: Boolean
        get() = true

    val decimalValue: BigDecimal

    val intValue: BigInteger

    companion object {

        fun of(decimal: BigDecimal): Real {
            return Real.of(decimal)
        }

        fun of(value: Number): Numeric {
            return Numeric.of(value.toString())
        }

        fun of(decimal: Double): Real {
            return Real.of(decimal)
        }

        fun of(decimal: Float): Real {
            return Real.of(decimal)
        }

        fun of(integer: BigInteger): Integral {
            return Integral.of(integer)
        }

        fun of(integer: Int): Integral {
            return Integral.of(integer)
        }

        fun of(integer: Long): Integral {
            return Integral.of(integer)
        }

        fun of(integer: Short): Integral {
            return Integral.of(integer)
        }

        fun of(integer: Byte): Integral {
            return Integral.of(integer)
        }

        fun of(number: String): Numeric {
            try {
                return Integral.of(number)
            } catch (ex: NumberFormatException) {
                return Real.of(number)
            }

        }
    }
}
