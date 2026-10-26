package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Term

class LogicProgrammingScopeWithOperatorsCharStringSpec :
    FunSpec({
        include(operatorsTests('C', "X", CharStringOperators))
    })

private object CharStringOperators : ArithmeticOperators<Char, String> {
    override fun LogicProgrammingScopeWithOperators<*>.plus(
        a: Char,
        b: String,
    ): Term = a + b

    override fun LogicProgrammingScopeWithOperators<*>.minus(
        a: Char,
        b: String,
    ): Term = a - b

    override fun LogicProgrammingScopeWithOperators<*>.times(
        a: Char,
        b: String,
    ): Term = a * b

    override fun LogicProgrammingScopeWithOperators<*>.div(
        a: Char,
        b: String,
    ): Term = a / b

    override fun LogicProgrammingScopeWithOperators<*>.rem(
        a: Char,
        b: String,
    ): Term = a % b
}
