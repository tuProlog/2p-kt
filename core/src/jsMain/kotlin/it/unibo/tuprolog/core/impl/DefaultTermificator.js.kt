@file:Suppress("MatchingDeclarationName")

package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.AbstractTermificator
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Termificator
import it.unibo.tuprolog.utils.NumberTypeTester

internal actual class DefaultTermificator actual constructor(
    scope: Scope,
    private val novel: Boolean,
) : AbstractTermificator(scope) {
    init {
        defaultConfiguration(novel)
    }

    private val tester = NumberTypeTester()

    override fun handleNumberAsNumeric(value: Number): Term =
        if (tester.numberIsInteger(value)) {
            scope.intOf(tester.numberToInteger(value))
        } else {
            scope.realOf(tester.numberToDecimal(value))
        }

    override fun copy(scope: Scope): Termificator = DefaultTermificator(scope, novel)
}
