package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Termificator

interface OOPContext {
    val termificator: Termificator

    val objectifier: Objectifier

    val typeFactory: TypeFactory

    val termFactory: OopTermFactory
}
