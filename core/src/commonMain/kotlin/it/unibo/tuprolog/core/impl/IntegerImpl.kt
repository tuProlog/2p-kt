package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

@Suppress("EqualsOrHashCode")
internal class IntegerImpl(override val value: BigInteger) : NumericImpl(), Integer {

    override val decimalValue: BigDecimal by lazy {
        BigDecimal.of(intValue)
    }

    override val intValue: BigInteger = value

    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Integer) return false

        return equalsToInteger(other)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun equalsToInteger(other: Integer) =
        value.compareTo(other.value) == 0

    override fun equals(other: Term, useVarCompleteName: Boolean): Boolean {
        return other is Integer && equalsToInteger(other)
    }

    override val hashCode: Int by lazy { value.hashCode() }

    override fun compareValueTo(other: Numeric): Int =
        when (other) {
            is Integer -> value.compareTo(other.value)
            else -> super<NumericImpl>.compareValueTo(other)
        }
}
