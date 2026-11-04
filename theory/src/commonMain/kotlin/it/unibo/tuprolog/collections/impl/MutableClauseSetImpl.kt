package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseSet
import it.unibo.tuprolog.collections.MutableClauseSet
import it.unibo.tuprolog.collections.store.FrozenStore
import it.unibo.tuprolog.collections.store.MutableStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal class MutableClauseSetImpl(
    store: MutableStore,
) : MutableClauseMultiSetImpl(store),
    MutableClauseSet {
    override fun add(clause: Clause): Boolean = !containsVariant(clause) && super.add(clause)

    override fun toMutable(): MutableClauseSet = of(unificator, toList())

    override fun toImmutable(): ClauseSet = FrozenClauseSet(FrozenStore.of(unificator, toList()))

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseSetImpl = MutableClauseSetImpl(MutableStore(unificator)).also { it.addAll(clauses) }
    }
}
