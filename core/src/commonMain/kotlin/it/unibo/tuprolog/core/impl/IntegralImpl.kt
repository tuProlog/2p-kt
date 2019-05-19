package it.unibo.tuprolog.core.impl

import io.github.gciatto.kt.math.BigDecimal
import io.github.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.Integral
import it.unibo.tuprolog.core.Numeric

internal class IntegralImpl(override val value: BigInteger) :  NumericImpl(), Integral {

    override val decimalValue: BigDecimal by lazy {
        BigDecimal.of(intValue)
    }

    override val intValue: BigInteger
        get() = value

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is NumericImpl) return false

        if (other is IntegralImpl) {
            return value.compareTo(other.value) == 0
        } else {
            return decimalValue.compareTo(other.decimalValue) == 0
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun compareTo(other: Numeric): Int {
        if (other is IntegralImpl) {
            return value.compareTo(other.value)
        } else {
            return super<NumericImpl>.compareTo(other)
        }
    }
}