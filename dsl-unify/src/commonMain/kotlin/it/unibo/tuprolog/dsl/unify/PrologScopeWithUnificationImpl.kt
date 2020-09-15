package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.dsl.PrologScope
import it.unibo.tuprolog.unify.Unificator

class PrologScopeWithUnificationImpl(unificator: Unificator) :
    PrologScopeWithUnification, PrologScope by PrologScope.empty(), Unificator by unificator {

    constructor() : this(Unificator.default)
}
