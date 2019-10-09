package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.unify.Unification

class PrologWithTheoriesImpl(unification: Unification) : PrologWithTheories, Prolog by Prolog.empty(), Unification by unification {

    constructor() : this(Unification.default)
}