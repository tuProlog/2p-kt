package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

object Factories {
    fun emptyMutableClauseMultiset(): MutableClauseMultiSet = MutableClauseMultiSet.empty(Unificator.default)

    fun mutableClauseMultisetOf(clauses: Iterable<Clause>): MutableClauseMultiSet =
        MutableClauseMultiSet.of(Unificator.default, clauses)

    fun emptyMutableClauseQueue(): MutableClauseQueue = MutableClauseQueue.empty(Unificator.default)

    fun mutableClauseQueueOf(clauses: Iterable<Clause>): MutableClauseQueue =
        MutableClauseQueue.of(Unificator.default, clauses)

    fun emptyClauseMultiset(): ClauseMultiSet = ClauseMultiSet.empty(Unificator.default)

    fun clauseMultisetOf(clauses: Iterable<Clause>): ClauseMultiSet =
        ClauseMultiSet.of(Unificator.default, clauses)

    fun emptyClauseQueue(): ClauseQueue = ClauseQueue.empty(Unificator.default)

    fun clauseQueueOf(clauses: Iterable<Clause>): ClauseQueue =
        ClauseQueue.of(Unificator.default, clauses)

    fun emptyIndexedTheory(): Theory = Theory.emptyIndexed(Unificator.default)

    fun indexedTheoryOf(clauses: Iterable<Clause>): Theory =
        Theory.indexedOf(Unificator.default, clauses)

    fun indexedTheoryOf(clauses: Array<Clause>): Theory =
        Theory.indexedOf(Unificator.default, *clauses)

    fun indexedTheoryOf(clauses: Sequence<Clause>): Theory =
        Theory.indexedOf(Unificator.default, clauses)

    fun indexedTheoryOf(clauses: Array<Scope.() -> Clause>): Theory =
        Theory.indexedOf(Unificator.default, *clauses)

    fun emptyMutableIndexedTheory(): MutableTheory = MutableTheory.emptyIndexed(Unificator.default)

    fun mutableIndexedTheoryOf(clauses: Iterable<Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, clauses)

    fun mutableIndexedTheoryOf(clauses: Array<Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, *clauses)

    fun mutableIndexedTheoryOf(clauses: Sequence<Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, clauses)

    fun mutableIndexedTheoryOf(clauses: Array<Scope.() -> Clause>): MutableTheory =
        MutableTheory.indexedOf(Unificator.default, *clauses)

    fun emptyListedTheory(): Theory = Theory.emptyListed(Unificator.default)

    fun listedTheoryOf(clauses: Iterable<Clause>): Theory =
        Theory.listedOf(Unificator.default, clauses)

    fun listedTheoryOf(clauses: Array<Clause>): Theory =
        Theory.listedOf(Unificator.default, *clauses)

    fun listedTheoryOf(clauses: Sequence<Clause>): Theory =
        Theory.listedOf(Unificator.default, clauses)

    fun listedTheoryOf(clauses: Array<Scope.() -> Clause>): Theory =
        Theory.listedOf(Unificator.default, *clauses)

    fun emptyMutableListedTheory(): MutableTheory = MutableTheory.emptyListed(Unificator.default)

    fun mutableListedTheoryOf(clauses: Iterable<Clause>): MutableTheory =
        MutableTheory.listedOf(Unificator.default, clauses)

    fun mutableListedTheoryOf(clauses: Array<Clause>): MutableTheory =
        MutableTheory.listedOf(Unificator.default, *clauses)

    fun mutableListedTheoryOf(clauses: Sequence<Clause>): MutableTheory =
        MutableTheory.listedOf(Unificator.default, clauses)

    fun mutableListedTheoryOf(clauses: Array<Scope.() -> Clause>): MutableTheory =
        MutableTheory.listedOf(Unificator.default, *clauses)
}
