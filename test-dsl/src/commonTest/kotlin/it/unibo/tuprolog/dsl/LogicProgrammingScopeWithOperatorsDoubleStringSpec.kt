package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Term

class LogicProgrammingScopeWithOperatorsDoubleStringSpec :
    FunSpec({
        include(operatorsTests(3.2, "X", DoubleStringOperators))
    })

private object DoubleStringOperators : ArithmeticOperators<Double, String> {
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
