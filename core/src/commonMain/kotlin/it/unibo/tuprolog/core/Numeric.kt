package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

interface Numeric : Term, Comparable<Numeric> {

    override val isNumber: Boolean
        get() = true

    override val isGround: Boolean
        get() = true

    val decimalValue: BigDecimal

    val intValue: BigInteger

    override fun compareTo(other: Numeric): Int = decimalValue.compareTo(other.decimalValue)

    companion object {

        fun of(decimal: BigDecimal): Real = Real.of(decimal)

        fun of(value: Number): Numeric = of(value.toString())

        fun of(decimal: Double): Real = Real.of(decimal)

        fun of(decimal: Float): Real = Real.of(decimal)

        fun of(integer: BigInteger): Integral = Integral.of(integer)

        fun of(integer: Int): Integral = Integral.of(integer)

        fun of(integer: Long): Integral = Integral.of(integer)

        fun of(integer: Short): Integral = Integral.of(integer)

        fun of(integer: Byte): Integral = Integral.of(integer)

        fun of(number: String): Numeric =
                try {
                    Integral.of(number)
                } catch (ex: NumberFormatException) {
                    Real.of(number)
                }
    }
}
