package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.store.ClauseSource
import it.unibo.tuprolog.collections.store.MutableStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal open class MutableClauseMultiSetImpl(
    protected val store: MutableStore,
) : StoreBackedCollection(),
    MutableClauseMultiSet {
    override val source: ClauseSource get() = store

    override fun add(clause: Clause): Boolean {
        store.add(clause)
        return true
    }

    override fun removeFirstMatching(query: Clause): Clause? = store.removeFirst(query)

    override fun removeAllMatching(query: Clause): List<Clause> = store.removeAll(query)

    override fun clear() = store.clear()

    override fun toMutable(): MutableClauseMultiSet = of(unificator, toList())

    override fun toImmutable(): ClauseMultiSet = FrozenClauseMultiSet.of(unificator, toList())

    override fun equals(other: Any?): Boolean = multiSetEquals(other)

    override fun hashCode(): Int = multiSetHashCode()

    companion object {
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseMultiSetImpl =
            MutableClauseMultiSetImpl(MutableStore(unificator).also { it.addAll(clauses.toList()) })
    }
}
