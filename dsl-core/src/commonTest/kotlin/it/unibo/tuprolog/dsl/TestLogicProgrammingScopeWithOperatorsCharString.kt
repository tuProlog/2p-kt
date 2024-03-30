package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Term

class TestLogicProgrammingScopeWithOperatorsCharString :
    AbstractTestLogicProgrammingScopeWithOperators<Char, String>(first = 'C', second = "X") {
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
