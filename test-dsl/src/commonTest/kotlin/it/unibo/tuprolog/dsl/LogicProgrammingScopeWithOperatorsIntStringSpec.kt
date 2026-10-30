package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Term

class LogicProgrammingScopeWithOperatorsIntStringSpec :
    FunSpec({
        include(operatorsTests(3, "X", IntStringOperators))
    })

private object IntStringOperators : ArithmeticOperators<Int, String> {
    override fun LogicProgrammingScopeWithOperators<*>.plus(
        a: Int,
        b: String,
    ): Term = a + b

    override fun LogicProgrammingScopeWithOperators<*>.minus(
        a: Int,
        b: String,
    ): Term = a - b

    override fun LogicProgrammingScopeWithOperators<*>.times(
        a: Int,
        b: String,
    ): Term = a * b

    override fun LogicProgrammingScopeWithOperators<*>.div(
        a: Int,
        b: String,
    ): Term = a / b

    override fun LogicProgrammingScopeWithOperators<*>.rem(
        a: Int,
        b: String,
    ): Term = a % b
}
