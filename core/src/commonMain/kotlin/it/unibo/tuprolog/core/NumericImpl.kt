package it.unibo.tuprolog.core

import io.github.gciatto.kt.math.BigDecimal
import io.github.gciatto.kt.math.BigInteger

internal abstract class NumericImpl : TermImpl(), Numeric {

    override fun structurallyEquals(other: Term): Boolean {
        return other is NumericImpl
                && decimalValue.compareTo(other.decimalValue) == 0
    }
    
    abstract override val decimalValue: BigDecimal

    abstract override val intValue: BigInteger

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other is NumericImpl) return false

        return decimalValue.compareTo((other as NumericImpl).decimalValue) == 0
    }

    override fun hashCode(): Int {
        return decimalValue.hashCode()
    }

    override fun toString(): String {
        return decimalValue.toString()
    }


}