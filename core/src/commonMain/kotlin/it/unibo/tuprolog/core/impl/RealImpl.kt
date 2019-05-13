package it.unibo.tuprolog.core.impl

import io.github.gciatto.kt.math.BigDecimal
import io.github.gciatto.kt.math.BigInteger
import it.unibo.tuprolog.core.Real

internal class RealImpl(override val value: BigDecimal) : NumericImpl(), Real {

    override val decimalValue: BigDecimal = value

    override val intValue: BigInteger by lazy {
        value.toBigInteger()
    }
}