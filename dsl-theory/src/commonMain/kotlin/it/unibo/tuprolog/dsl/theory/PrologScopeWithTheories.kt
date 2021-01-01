package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.PrologScope
import it.unibo.tuprolog.dsl.unify.PrologScopeWithUnification
import it.unibo.tuprolog.theory.Theory
import kotlin.js.JsName

interface PrologScopeWithTheories : PrologScopeWithUnification {
    @JsName("theoryOf")
    fun theoryOf(vararg clause: Clause): Theory {
        return Theory.indexedOf(*clause)
    }

    @JsName("theoryOfIterable")
    fun theoryOf(clauses: Iterable<Clause>, vararg otherClauses: Iterable<Clause>): Theory {
        return Theory.indexedOf(sequenceOf(clauses, *otherClauses).flatMap { it.asSequence() })
    }

    @JsName("theoryOfSequence")
    fun theoryOf(clauses: Sequence<Clause>, vararg otherClauses: Sequence<Clause>): Theory {
        return Theory.indexedOf(sequenceOf(clauses, *otherClauses).flatten())
    }

    @JsName("theory")
    fun theory(vararg clauseFunctions: PrologScope.() -> Any): Theory = theoryOf(
        *clauseFunctions.map { function ->
            PrologScope.empty().function().let { clause { it } }
        }.toTypedArray()
    )

    companion object {
        @JsName("empty")
        fun empty(): PrologScopeWithTheories = PrologScopeWithTheoriesImpl()
    }
}
