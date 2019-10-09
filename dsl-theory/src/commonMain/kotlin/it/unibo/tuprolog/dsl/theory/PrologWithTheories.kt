package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.dsl.unify.PrologWithUnification
import it.unibo.tuprolog.unify.Unification

interface PrologWithTheories : PrologWithUnification {



    companion object {
        fun empty(): PrologWithTheories = TODO()
    }
}


fun <R> prolog(function: PrologWithTheories.() -> R): R {
    return PrologWithTheories.empty().function()
}