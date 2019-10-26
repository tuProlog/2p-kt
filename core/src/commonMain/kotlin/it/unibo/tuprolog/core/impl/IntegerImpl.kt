package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

internal class IntegerImpl(override val value: BigInteger) : NumericImpl(), Integer {

    override val decimalValue: BigDecimal by lazy {
        BigDecimal.of(intValue)
    }

    override val intValue: BigInteger = value

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        return value.compareTo((other as IntegerImpl).value) == 0
    }

    override fun hashCode(): Int = value.hashCode()

    override fun compareTo(other: Numeric): Int =
            when (other) {
                is IntegerImpl -> value.compareTo(other.value)
                else -> super<NumericImpl>.compareTo(other)
            }
}
