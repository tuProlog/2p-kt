package it.unibo.tuprolog.core

import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

interface Numeric : Constant, Comparable<Numeric> {

    override val isNumber: Boolean
        get() = true

    override val variables: Sequence<Var>
        get() = emptySequence()

    val decimalValue: BigDecimal

    val intValue: BigInteger

    override fun freshCopy(): Numeric = this

    override fun freshCopy(scope: Scope): Numeric = this

    override fun compareTo(other: Numeric): Int = decimalValue.compareTo(other.decimalValue)

    companion object {

        fun of(decimal: BigDecimal): Real = Real.of(decimal)
        fun of(decimal: Double): Real = Real.of(decimal)
        fun of(decimal: Float): Real = Real.of(decimal)
        fun of(integer: BigInteger): Integer = Integer.of(integer)
        fun of(integer: Int): Integer = Integer.of(integer)
        fun of(integer: Long): Integer = Integer.of(integer)
        fun of(integer: Short): Integer = Integer.of(integer)
        fun of(integer: Byte): Integer = Integer.of(integer)

        fun of(value: Number): Numeric = when (value) {
            // avoiding string format is necessary for "floats", to maintain full precision during conversions
            is Float -> of(value)
            else -> of(value.toString())
        }

        fun of(number: String): Numeric =
            try {
                Integer.of(number)
            } catch (ex: NumberFormatException) {
                Real.of(number)
            }
    }
}
