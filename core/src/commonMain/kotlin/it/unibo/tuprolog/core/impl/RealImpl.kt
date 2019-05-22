package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Real
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

internal class RealImpl(override val value: BigDecimal) : NumericImpl(), Real {

    override val decimalValue: BigDecimal = value

    override val intValue: BigInteger by lazy {
        value.toBigInteger()
    }
}