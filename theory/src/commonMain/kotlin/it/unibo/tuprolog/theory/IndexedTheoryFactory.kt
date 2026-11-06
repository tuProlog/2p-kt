package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.impl.IndexedTheory
import it.unibo.tuprolog.theory.impl.MutableIndexedTheory
import it.unibo.tuprolog.unify.Unificator

/**
 * A [TheoryFactory] producing the default [Theory]/[MutableTheory] implementations, the ones [Theory.of] and
 * [MutableTheory.of] return: clauses are grouped by procedure, and indexed on demand on whichever argument (or
 * sub-argument) a lookup binds, so retrieval only unifies the (typically few) clauses that could actually unify with a
 * goal. Immutable theories are optimised for reads, mutable ones for updates. See [Theory] for the indexing scheme
 * and its references, and [MutableTheory] for the logical update view.
 *
 * @param unificator the default [Unificator] of the theories built without an explicit one
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
