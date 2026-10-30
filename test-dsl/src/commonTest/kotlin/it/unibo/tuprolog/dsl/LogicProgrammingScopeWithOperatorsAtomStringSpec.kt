package it.unibo.tuprolog.dsl

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

class LogicProgrammingScopeWithOperatorsAtomStringSpec :
    FunSpec({
        include(operatorsTests(Atom.of("a"), "X", AtomStringOperators))
    })

private object AtomStringOperators : ArithmeticOperators<Atom, String> {
    override fun LogicProgrammingScopeWithOperators<*>.plus(
        a: Atom,
        b: String,
    ): Term = a + b

    override fun LogicProgrammingScopeWithOperators<*>.minus(
        a: Atom,
        b: String,
    ): Term = a - b

    override fun LogicProgrammingScopeWithOperators<*>.times(
        a: Atom,
        b: String,
    ): Term = a * b

    override fun LogicProgrammingScopeWithOperators<*>.div(
        a: Atom,
        b: String,
    ): Term = a / b

    override fun LogicProgrammingScopeWithOperators<*>.rem(
        a: Atom,
        b: String,
    ): Term = a % b
}
