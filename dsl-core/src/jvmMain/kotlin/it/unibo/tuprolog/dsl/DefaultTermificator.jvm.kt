@file:Suppress("MatchingDeclarationName")

package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import org.gciatto.kt.math.toKotlin
import java.math.BigDecimal as JavaBigDecimal
import java.math.BigInteger as JavaBigInteger

internal actual class DefaultTermificator actual constructor(
    scope: Scope,
    private val novel: Boolean,
) : AbstractTermificator(scope) {
    init {
        defaultConfiguration(novel)
    }

    actual override fun handleNumberAsNumeric(value: Number): Term =
        when (value) {
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

    actual override fun copy(scope: Scope): Termificator = DefaultTermificator(scope, novel)
}
