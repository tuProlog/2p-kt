package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Term

class TestLogicProgrammingScopeWithOperatorsIntString :
    AbstractTestLogicProgrammingScopeWithOperators<Int, String>(
        first = 3,
        second = "X",
    ) {
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
