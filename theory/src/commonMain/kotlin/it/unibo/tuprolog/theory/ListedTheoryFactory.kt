package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.impl.ListedTheory
import it.unibo.tuprolog.theory.impl.MutableListedTheory
import it.unibo.tuprolog.unify.Unificator

class ListedTheoryFactory(
    override val unificator: Unificator,
) : TheoryFactory {
    override fun copy(unificator: Unificator): TheoryFactory = ListedTheoryFactory(unificator)

    override fun emptyTheory(unificator: Unificator): Theory = ListedTheory(unificator, emptyList())

    override fun theoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): Theory = ListedTheory(unificator, clauses)

    override fun theoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): Theory = ListedTheory(unificator, clauses.asIterable())

    override fun theoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): Theory = ListedTheory(unificator, clauses)

    override fun emptyMutableTheory(unificator: Unificator): MutableTheory =
        MutableListedTheory(unificator, emptyList())

    override fun mutableTheoryOf(
        clauses: Iterable<Clause>,
        unificator: Unificator,
    ): MutableTheory = MutableListedTheory(unificator, clauses)

    override fun mutableTheoryOf(
        unificator: Unificator,
        vararg clauses: Clause,
    ): MutableTheory = MutableListedTheory(unificator, clauses.asIterable())

    override fun mutableTheoryOf(
        clauses: Sequence<Clause>,
        unificator: Unificator,
    ): MutableTheory = MutableListedTheory(unificator, clauses)

    object Default : TheoryFactory by ListedTheoryFactory(Unificator.default)
}
