package it.unibo.tuprolog.core

import io.github.gciatto.kt.math.BigDecimal
import io.github.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.impl.RealImpl

interface Real : Numeric {

    override val isReal: Boolean
        get() = true

    val value: BigDecimal

    override val decimalValue: BigDecimal
        get() = value

    override val intValue: BigInteger
        get() = value.toBigInteger()

    companion object {

        fun of(real: BigDecimal): Real {
            return RealImpl(real)
        }

        fun of(real: Double): Real {
            return RealImpl(BigDecimal.of(real))
        }

        fun of(real: Float): Real {
            return RealImpl(BigDecimal.of(real))
        }

        fun of(real: String): Real {
            return RealImpl(BigDecimal.of(real))
        }
    }
}
