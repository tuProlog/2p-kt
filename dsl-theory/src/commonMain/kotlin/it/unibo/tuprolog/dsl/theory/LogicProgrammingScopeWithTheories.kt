package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.dsl.unify.LogicProgrammingScopeWithUnificator
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryFactory
import kotlin.js.JsName

interface LogicProgrammingScopeWithTheories<S : LogicProgrammingScopeWithTheories<S>> :
    LogicProgrammingScopeWithUnificator<S>,
    TheoryFactory {
    @JsName("theoryFactory")
    val theoryFactory: TheoryFactory

    @JsName("convertToClause")
    fun Any.toClause(): Clause = toSpecificSubTypeOfTerm(Clause::class, ::clauseOf)

    @JsName("theory")
    fun theory(vararg clauseFunctions: S.() -> Any): Theory =
        theoryOf(clauseFunctions.map { newScope().it().toClause() })

    @JsName("theoryOfConcatenatedSequences")
    fun theoryOf(
        clauses: Sequence<Clause>,
        vararg otherClauses: Sequence<Clause>,
    ): Theory = theoryOf(clauses + otherClauses.flatMap { it })

    @JsName("theoryOfConcatenatedIterables")
    fun theoryOf(
        clauses: Iterable<Clause>,
        vararg otherClauses: Iterable<Clause>,
    ): Theory = theoryOf(kotlin.collections.listOf(clauses, *otherClauses).flatten())

    @JsName("mutableTheory")
    fun mutableTheory(vararg clauseFunctions: S.() -> Any): MutableTheory =
        mutableTheoryOf(clauseFunctions.map { newScope().it().toClause() })

    @JsName("mutableTheoryOfConcatenatedSequences")
    fun mutableTheoryOf(
        clauses: Sequence<Clause>,
        vararg otherClauses: Sequence<Clause>,
    ): MutableTheory = mutableTheoryOf(clauses + otherClauses.flatMap { it })

    @JsName("mutableTheoryOfConcatenatedIterables")
    fun mutableTheoryOf(
        clauses: Iterable<Clause>,
        vararg otherClauses: Iterable<Clause>,
    ): MutableTheory = mutableTheoryOf(kotlin.collections.listOf(clauses, *otherClauses).flatten())
}
