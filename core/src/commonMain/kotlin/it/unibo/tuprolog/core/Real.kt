package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.RegexUtils.DEC
import it.unibo.tuprolog.core.RegexUtils.EXP
import it.unibo.tuprolog.core.RegexUtils.INT
import it.unibo.tuprolog.core.impl.RealImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

interface Real : Numeric {

    override val isReal: Boolean
        get() = true

    override val value: BigDecimal

    override val decimalValue: BigDecimal
        get() = value

    override val intValue: BigInteger
        get() = value.toBigInteger()

    override fun freshCopy(): Real = this

    override fun freshCopy(scope: Scope): Real = this

    companion object {

        @JvmField
        val REAL_REGEX_PATTERN = "^[+\\-]?(($INT$DEC$EXP?)|($INT$EXP)|($DEC$EXP?))$".toRegex()

        @JvmStatic
        fun of(real: BigDecimal): Real = RealImpl(real)

        @JvmStatic
        fun of(real: Double): Real = of(BigDecimal.of(real))

        @JvmStatic
        fun of(real: Float): Real = of(BigDecimal.of(real))

        @JvmStatic
        fun of(real: String): Real = of(BigDecimal.of(real))
    }
}

