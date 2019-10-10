package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.dsl.Prolog
import it.unibo.tuprolog.dsl.unify.PrologWithUnification
import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.unify.Unification

interface PrologWithTheories : PrologWithUnification {

    fun theoryOf(vararg clause: Clause): ClauseDatabase {
        return ClauseDatabase.of(*clause)
    }

    companion object {
        fun empty(): PrologWithTheories = PrologWithTheoriesImpl()
    }
}


fun <R> prolog(function: PrologWithTheories.() -> R): R {
    return PrologWithTheories.empty().function()
}