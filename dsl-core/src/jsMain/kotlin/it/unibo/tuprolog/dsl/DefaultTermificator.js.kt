package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.NumberTypeTester

internal actual class DefaultTermificator actual constructor(scope: Scope) : AbstractTermificator(scope) {
    private val tester = NumberTypeTester()

    override fun handleNumberAsNumeric(value: Number): Term =
        if (tester.numberIsInteger(value)) {
            scope.intOf(tester.numberToInteger(value))
        } else {
            scope.realOf(tester.numberToDecimal(value))
        }
}
