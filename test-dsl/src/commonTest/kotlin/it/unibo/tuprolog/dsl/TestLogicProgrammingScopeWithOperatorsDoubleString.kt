package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Term

class TestLogicProgrammingScopeWithOperatorsDoubleString :
    AbstractTestLogicProgrammingScopeWithOperators<Double, String>(first = 3.2, second = "X") {
    override fun LogicProgrammingScopeWithOperators<*>.plus(
        a: Double,
        b: String,
    ): Term = a + b

    override fun LogicProgrammingScopeWithOperators<*>.minus(
        a: Double,
        b: String,
    ): Term = a - b

    override fun LogicProgrammingScopeWithOperators<*>.times(
        a: Double,
        b: String,
    ): Term = a * b

    override fun LogicProgrammingScopeWithOperators<*>.div(
        a: Double,
        b: String,
    ): Term = a / b

    override fun LogicProgrammingScopeWithOperators<*>.rem(
        a: Double,
        b: String,
    ): Term = a % b
}
