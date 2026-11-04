package it.unibo.tuprolog.testutils

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

/** Theory factories with the default unificator, for the shared theory test suites. */
object Factories {
    fun emptyIndexedTheory(): Theory = Theory.emptyIndexed(Unificator.default)

    fun indexedTheoryOf(clauses: Iterable<Clause>): Theory = Theory.indexedOf(Unificator.default, clauses)

    fun indexedTheoryOf(clauses: Array<Clause>): Theory = Theory.indexedOf(Unificator.default, *clauses)

    fun indexedTheoryOf(clauses: Sequence<Clause>): Theory = Theory.indexedOf(Unificator.default, clauses)

    fun indexedTheoryOf(clauses: Array<Scope.() -> Clause>): Theory = Theory.indexedOf(Unificator.default, *clauses)

    fun emptyMutableIndexedTheory(): MutableTheory = MutableTheory.emptyIndexed(Unificator.default)

    fun mutableIndexedTheoryOf(clauses: Iterable<Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, clauses)

    fun mutableIndexedTheoryOf(clauses: Array<Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, *clauses)

    fun mutableIndexedTheoryOf(clauses: Sequence<Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, clauses)

    fun mutableIndexedTheoryOf(clauses: Array<Scope.() -> Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, *clauses)
}
