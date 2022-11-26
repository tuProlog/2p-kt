package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.LogicProgrammingScope
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnification
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface LogicProgrammingScopeWithTheories : LogicProgrammingScopeWithUnification {

    @JsName("emptyTheory")
    fun emptyTheory(): Theory {
        return Theory.emptyIndexed(unificator)
    }

    @JsName("theoryOf")
    fun theoryOf(vararg clause: Clause): Theory {
        return Theory.indexedOf(unificator, *clause)
    }

    @JsName("theoryOfIterable")
    fun theoryOf(clauses: Iterable<Clause>, vararg otherClauses: Iterable<Clause>): Theory {
        return Theory.indexedOf(unificator, sequenceOf(clauses, *otherClauses).flatMap { it.asSequence() })
    }

    @JsName("theoryOfSequence")
    fun theoryOf(clauses: Sequence<Clause>, vararg otherClauses: Sequence<Clause>): Theory {
        return Theory.indexedOf(unificator, sequenceOf(clauses, *otherClauses).flatten())
    }

    @JsName("theory")
    fun theory(vararg clauseFunctions: LogicProgrammingScope.() -> Any): Theory = theoryOf(
        *clauseFunctions.map { function ->
            LogicProgrammingScope.empty().function().let { clause { it } }
        }.toTypedArray()
    )

    @JsName("emptyMutableTheory")
    fun emptyMutableTheory(): MutableTheory {
        return MutableTheory.emptyIndexed(unificator)
    }

    @JsName("mutableTheoryOf")
    fun mutableTheoryOf(vararg clause: Clause): MutableTheory {
        return MutableTheory.indexedOf(unificator, *clause)
    }

    @JsName("mutableTheoryOfIterable")
    fun mutableTheoryOf(clauses: Iterable<Clause>, vararg otherClauses: Iterable<Clause>): MutableTheory {
        return MutableTheory.indexedOf(unificator, sequenceOf(clauses, *otherClauses).flatMap { it.asSequence() })
    }

    @JsName("mutableTheoryOfSequence")
    fun mutableTheoryOf(clauses: Sequence<Clause>, vararg otherClauses: Sequence<Clause>): MutableTheory {
        return MutableTheory.indexedOf(unificator, sequenceOf(clauses, *otherClauses).flatten())
    }

    @JsName("mutableTheory")
    fun mutableTheory(vararg clauseFunctions: LogicProgrammingScope.() -> Any): MutableTheory = mutableTheoryOf(
        *clauseFunctions.map { function ->
            LogicProgrammingScope.empty().function().let { clause { it } }
        }.toTypedArray()
    )

    companion object {
        @JsName("of")
        fun of(
            unificator: Unificator = Unificator.default
        ): LogicProgrammingScopeWithTheories = LogicProgrammingScopeWithTheoriesImpl(unificator)
    }
}
