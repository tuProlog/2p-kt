package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Real
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

internal class RealImpl(override val value: BigDecimal) : NumericImpl(), Real {

    override val decimalValue: BigDecimal = value

    override val intValue: BigInteger by lazy {
        value.toBigInteger()
    }

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Real) return false

        return value.compareTo(other.value) == 0
    }

    override fun hashCode(): Int = value.stripTrailingZeros().hashCode()
}
