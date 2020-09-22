package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.dsl.PrologScope
import it.unibo.tuprolog.unify.Unificator

class PrologScopeWithTheoriesImpl(unificator: Unificator) :
    PrologScopeWithTheories, PrologScope by PrologScope.empty(), Unificator by unificator {

    constructor() : this(Unificator.default)
}
