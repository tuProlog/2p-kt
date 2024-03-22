package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import java.math.BigInteger as JavaBigInteger
import java.math.BigDecimal as JavaBigDecimal
import org.gciatto.kt.math.toKotlin

internal actual class DefaultTermificator actual constructor(scope: Scope) : AbstractTermificator(scope) {
    override fun handleNumberAsNumeric(value: Number): Term = when (value) {
        is Int -> scope.numOf(value)
        is Long -> scope.numOf(value)
        is JavaBigInteger -> scope.intOf(value.toKotlin())
        is Short -> scope.numOf(value)
        is Byte -> scope.numOf(value)
        is Double -> scope.realOf(value.toString())
        is Float -> scope.realOf(value.toString())
        is JavaBigDecimal -> scope.realOf(value.toKotlin())
        else -> Numeric.of(value.toString())
    }
}
