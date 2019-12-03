package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.unify.Unificator

class PrologWithUnificationImpl(unificator: Unificator) :
    PrologWithUnification, Prolog by Prolog.empty(), Unificator by unificator {

    constructor() : this(Unificator.default)
}