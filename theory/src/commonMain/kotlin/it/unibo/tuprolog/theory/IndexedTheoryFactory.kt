package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.theory.impl.MutableIndexedTheory
import it.unibo.tuprolog.unify.Unificator

/**
 * A [TheoryFactory] that produces [Theory]/[MutableTheory] instances backed by an indexed data structure
 * (see [it.unibo.tuprolog.collections.rete.custom.ReteTree]): clauses are discriminated by directive-vs-rule,
 * functor, arity and first-argument shape, so retrieval only re-checks the (typically small) subset of clauses
 * that could actually unify with a goal, at the cost of extra bookkeeping on every insertion/removal. This is
 * the right choice for knowledge bases that are built once (or rarely changed) and queried many times — e.g. a
 * solver's static knowledge base.
 * @see ListedTheoryFactory
 */
class IndexedTheoryFactory(
    override val unificator: Unificator,
) : TheoryFactory {
    override fun copy(unificator: Unificator): TheoryFactory = IndexedTheoryFactory(unificator)

    override fun emptyTheory(unificator: Unificator): Theory = IndexedTheory(unificator, emptyList())

    override fun theoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): Theory = IndexedTheory(unificator, clauses)

    override fun theoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): Theory = IndexedTheory(unificator, clauses.asIterable())

    override fun theoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): Theory = IndexedTheory(unificator, clauses)

    override fun emptyMutableTheory(unificator: Unificator): MutableTheory =
        MutableIndexedTheory(unificator, emptyList())

    override fun mutableTheoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): MutableTheory = MutableIndexedTheory(unificator, clauses)

    override fun mutableTheoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): MutableTheory = MutableIndexedTheory(unificator, clauses.asIterable())

    override fun mutableTheoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): MutableTheory = MutableIndexedTheory(unificator, clauses)

    /** The default [IndexedTheoryFactory], using [Unificator.default]. */
    object Default : TheoryFactory by IndexedTheoryFactory(Unificator.default)
}
