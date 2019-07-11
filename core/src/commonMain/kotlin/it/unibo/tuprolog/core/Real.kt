package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RealImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger

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
        private const val i = """([0-9]+)"""
        private const val d = """(\.[0-9]+)"""
        private const val e = """([eE][+\-]?[0-9]+)"""
        val REAL_REGEX_PATTERN = "^[+\\-]?(($i$d$e?)|($i$e)|($d$e?))$".toRegex()

        fun of(real: BigDecimal): Real = RealImpl(real)
        fun of(real: Double): Real = of(BigDecimal.of(real))
        fun of(real: Float): Real = of(BigDecimal.of(real))
        fun of(real: String): Real = of(BigDecimal.of(real))
    }
}
