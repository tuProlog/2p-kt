package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/**
 * Rejects, without unifying, the clauses of a procedure whose head has an exact key differing from the query's on some
 * argument (the key pre-check of WAM and SWI-Prolog indexing). Indexes select candidates on one argument only: this
 * checks all the others, before anything costlier (unification, renaming) happens. One filter serves one iteration:
 * it accepts the first candidate unchecked, and computes the query's keys when a second one shows up, so that lookups
 * with a single candidate (the common case, with a selective index) pay nothing for it.
 */
internal class KeyFilter private constructor(
    private val unificator: Unificator,
    private val head: Struct,
) {
    private var seen = false

    // the query's exact key of each argument, null where it has none: computed on the second candidate
    private var keys: Array<IndexKey?>? = null

    fun accepts(clause: Clause): Boolean {
        if (!seen) {
            seen = true
            return true
        }
        val keys = keys ?: Array(head.arity) { unificator.indexKey(head.getArgAt(it)).takeIf { key -> key.isExact } }
        this.keys = keys
        val other = clause.head ?: return true
        for (i in keys.indices) {
            val key = keys[i] ?: continue
            val otherKey = unificator.indexKey(other.getArgAt(i))
            if (otherKey.isExact && otherKey != key) return false
        }
        return true
    }

    companion object {
        private val NONE: (Clause) -> Boolean = { true }

        /** A new filter for one iteration over the candidates for [query]. */
        fun of(
            unificator: Unificator,
            query: Clause,
        ): (Clause) -> Boolean = query.indexableHead()?.let { KeyFilter(unificator, it)::accepts } ?: NONE
    }
}
