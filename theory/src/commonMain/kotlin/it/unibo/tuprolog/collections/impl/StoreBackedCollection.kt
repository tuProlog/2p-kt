package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.store.ClauseSource
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
/*
 * Clause collections (#1019) are thin façades over the clause stores: a FrozenStore for immutable ones, a MutableStore
 * for mutable ones. Ordered and unordered collections share the stores (unordered ones simply promise no order).
 */

internal abstract class StoreBackedCollection : ClauseCollection {
    protected abstract val source: ClauseSource

    override val unificator: Unificator get() = source.unificator

    override val size: Long get() = source.size.toLong()

    override val isEmpty: Boolean get() = source.size == 0

    override fun iterator(): Iterator<Clause> = source.ordered().iterator()

    override fun get(clause: Clause): Sequence<Clause> = source.matching(clause)

    override fun toString(): String = joinToString(", ", "${this::class.simpleName}[", "]")
}
