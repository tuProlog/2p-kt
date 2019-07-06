package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Integral
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

internal class IntegralImpl(override val value: BigInteger) : NumericImpl(), Integral {

    override val decimalValue: BigDecimal by lazy {
        BigDecimal.of(intValue)
    }

    override fun strictlyEquals(other: Term): Boolean =
            other is IntegralImpl && other::class == this::class
                    && decimalValue.compareTo(other.decimalValue) == 0

    override val intValue: BigInteger
        get() = value

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is NumericImpl) return false

        return when (other) {
            is IntegralImpl -> value.compareTo(other.value) == 0
            else -> decimalValue.compareTo(other.decimalValue) == 0
        }
    }

    override fun hashCode(): Int = value.hashCode()

    override fun compareTo(other: Numeric): Int =
            when (other) {
                is IntegralImpl -> value.compareTo(other.value)
                else -> super<NumericImpl>.compareTo(other)
            }
}