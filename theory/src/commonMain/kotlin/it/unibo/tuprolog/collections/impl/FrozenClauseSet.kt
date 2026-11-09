package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseSet
import it.unibo.tuprolog.collections.MutableClauseSet
import it.unibo.tuprolog.collections.store.FrozenStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal class FrozenClauseSet(
    store: FrozenStore,
) : FrozenClauseMultiSet(store),
    ClauseSet {
    override fun toMutable(): MutableClauseSet = MutableClauseSetImpl.of(unificator, store.clauses.asList())

    override fun toImmutable(): ClauseSet = this

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): FrozenClauseSet =
            FrozenClauseSet(FrozenStore.of(unificator, MutableClauseSetImpl.of(unificator, clauses).toList()))
    }
}
