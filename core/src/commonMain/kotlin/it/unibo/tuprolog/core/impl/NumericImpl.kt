package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

internal abstract class NumericImpl : TermImpl(), Numeric {

    override fun structurallyEquals(other: Term): Boolean =
            other is NumericImpl
                    && decimalValue.compareTo(other.decimalValue) == 0

    override fun strictlyEquals(other: Term): Boolean =
            other is NumericImpl
                    && decimalValue.compareTo(other.decimalValue) == 0

    abstract override val decimalValue: BigDecimal

    abstract override val intValue: BigInteger

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is NumericImpl) return false

        return decimalValue.compareTo(other.decimalValue) == 0
    }

    override fun hashCode(): Int = decimalValue.hashCode()

    override fun toString(): String = decimalValue.toString()
}
