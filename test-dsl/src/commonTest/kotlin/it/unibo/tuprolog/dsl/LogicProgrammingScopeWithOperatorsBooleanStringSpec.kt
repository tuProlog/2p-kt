package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Term

class LogicProgrammingScopeWithOperatorsBooleanStringSpec :
    FunSpec({
        include(operatorsTests(false, "X", BooleanStringOperators))
    })

private object BooleanStringOperators : ArithmeticOperators<Boolean, String> {
    override fun LogicProgrammingScopeWithOperators<*>.plus(
        a: Boolean,
        b: String,
    ): Term = a + b

    override fun LogicProgrammingScopeWithOperators<*>.minus(
        a: Boolean,
        b: String,
    ): Term = a - b

    override fun LogicProgrammingScopeWithOperators<*>.times(
        a: Boolean,
        b: String,
    ): Term = a * b

    override fun LogicProgrammingScopeWithOperators<*>.div(
        a: Boolean,
        b: String,
    ): Term = a / b

    override fun LogicProgrammingScopeWithOperators<*>.rem(
        a: Boolean,
        b: String,
    ): Term = a % b
}
