package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.unify.Unificator

class PrologWithTheoriesImpl(unificator: Unificator) :
    PrologWithTheories, Prolog by Prolog.empty(), Unificator by unificator {

    constructor() : this(Unificator.default)
}