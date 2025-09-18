package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term

class TestLogicProgrammingScopeWithOperatorsAtomString :
    AbstractTestLogicProgrammingScopeWithOperators<Atom, String>(
        first = Atom.of("a"),
        second = "X",
    ) {
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
