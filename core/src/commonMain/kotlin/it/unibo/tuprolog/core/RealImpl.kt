package it.unibo.tuprolog.core

import io.github.gciatto.kt.math.BigDecimal
import io.github.gciatto.kt.math.BigInteger

internal class RealImpl(override val value: BigDecimal) : NumericImpl(), Real {

    override val decimalValue: BigDecimal = value

    override val intValue: BigInteger by lazy {
        value.toBigInteger()
    }
}