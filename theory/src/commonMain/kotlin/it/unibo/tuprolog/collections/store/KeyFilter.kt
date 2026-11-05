package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/**
 * Rejects, without unifying, the clauses of a procedure whose head has an exact key differing from the query's on some
 * argument (the key pre-check of WAM and SWI-Prolog indexing). Indexes select candidates on one argument only: this
 * checks all the others, before anything costlier (unification, renaming) happens.
 */
internal class KeyFilter private constructor(
    private val unificator: Unificator,
    // the query's exact key of each argument, null where it has none
    private val keys: Array<IndexKey?>,
) {
    fun accepts(clause: Clause): Boolean {
        val head = clause.head ?: return true
        for (i in keys.indices) {
            val key = keys[i] ?: continue
            val other = unificator.indexKey(head.getArgAt(i))
            if (other.isExact && other != key) return false
        }
        return true
    }

    companion object {
        private val NONE: (Clause) -> Boolean = { true }

        /** The filter for [query], as a predicate: one accepting everything if the query binds no argument. */
        fun of(
            unificator: Unificator,
            query: Clause,
        ): (Clause) -> Boolean {
            val head = query.indexableHead() ?: return NONE
            val keys = Array(head.arity) { unificator.indexKey(head.getArgAt(it)).takeIf { key -> key.isExact } }
            return if (keys.all { it == null }) NONE else KeyFilter(unificator, keys)::accepts
        }
    }
}
