package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseList
import it.unibo.tuprolog.collections.MutableClauseList
import it.unibo.tuprolog.collections.store.ClauseSource
import it.unibo.tuprolog.collections.store.FrozenStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal class FrozenClauseList(
    private val store: FrozenStore,
) : StoreBackedCollection(),
    ClauseList {
    override val source: ClauseSource get() = store

    override fun asList(): List<Clause> = store.clauses.asList()

    override fun firstOrNull(): Clause? = store.clauses.firstOrNull()

    override fun lastOrNull(): Clause? = store.clauses.lastOrNull()

    override fun toMutable(): MutableClauseList = MutableClauseListImpl.of(unificator, asList())

    override fun toImmutable(): ClauseList = this

    override fun equals(other: Any?): Boolean = orderedEquals(other)

    override fun hashCode(): Int = orderedHashCode()

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): FrozenClauseList = FrozenClauseList(FrozenStore.of(unificator, clauses.toList()))
    }
}
