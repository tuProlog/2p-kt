package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.store.ClauseSource
import it.unibo.tuprolog.collections.store.FrozenStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal open class FrozenClauseMultiSet(
    protected val store: FrozenStore,
) : StoreBackedCollection(),
    ClauseMultiSet {
    override val source: ClauseSource get() = store

    override fun toMutable(): MutableClauseMultiSet = MutableClauseMultiSetImpl.of(unificator, store.clauses.asList())

    override fun toImmutable(): ClauseMultiSet = this

    override fun equals(other: Any?): Boolean = multiSetEquals(other)

    override fun hashCode(): Int = multiSetHashCode()

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): FrozenClauseMultiSet = FrozenClauseMultiSet(FrozenStore.of(unificator, clauses.toList()))
    }
}
