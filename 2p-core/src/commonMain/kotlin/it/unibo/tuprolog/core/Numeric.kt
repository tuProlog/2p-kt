package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

interface Numeric : Term {
    override val isNumber: Boolean
        get() = true

    val decimalValue: BigDecimal

    val intValue: BigInteger

    companion object {

        fun of(decimal: BigDecimal): Numeric {
            return Real.of(decimal)
        }

        fun of(decimal: Double): Numeric {
            return Real.of(decimal)
        }

        fun of(decimal: Float): Numeric {
            return Real.of(decimal)
        }

        fun of(integer: BigInteger): Numeric {
            return Integral.of(integer)
        }

        fun of(integer: Long): Numeric {
            return Integral.of(integer)
        }

        fun of(integer: Short): Numeric {
            return Integral.of(integer)
        }

        fun of(integer: Byte): Numeric {
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
