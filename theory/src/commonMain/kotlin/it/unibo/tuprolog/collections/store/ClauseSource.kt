package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

/** What clause collections and theories read from a store. */
internal interface ClauseSource {
    val unificator: Unificator

    /** Number of clauses. */
    val size: Int

    /** All clauses, in order (for mutable stores, as of the call to `iterator()`). */
    fun ordered(): Sequence<Clause>

    /** The clauses unifying with [query], in order (for mutable stores, as of this call), lazily. */
    fun matching(query: Clause): Sequence<Clause>
}
