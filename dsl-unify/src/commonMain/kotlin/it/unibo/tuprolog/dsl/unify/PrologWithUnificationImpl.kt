package it.unibo.tuprolog.dsl.unify

import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.unify.Unification

class PrologWithUnificationImpl(unification: Unification) :
    PrologWithUnification, Prolog by Prolog.empty(), Unification by unification {

    constructor() : this(Unification.default)
}