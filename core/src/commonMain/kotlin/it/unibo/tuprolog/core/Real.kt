package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RealImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

interface Real : Numeric {

    override val isReal: Boolean
        get() = true

    val value: BigDecimal

    override val decimalValue: BigDecimal
        get() = value

    override val intValue: BigInteger
        get() = value.toBigInteger()

    companion object {

        fun of(real: BigDecimal): Real = RealImpl(real)

        fun of(real: Double): Real = RealImpl(BigDecimal.of(real))

        fun of(real: Float): Real = RealImpl(BigDecimal.of(real))

        fun of(real: String): Real = RealImpl(BigDecimal.of(real))
    }
}
