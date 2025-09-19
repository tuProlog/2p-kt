package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Term

class TestLogicProgrammingScopeWithOperatorsBooleanString :
    AbstractTestLogicProgrammingScopeWithOperators<Boolean, String>(first = false, second = "X") {
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
