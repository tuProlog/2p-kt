package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/**
 * Rejects, without unifying, the clauses of a procedure whose head has an exact key differing from the query's on some
 * head argument (a key pre-check, like the WAM's `switch_on_term` and SWI-Prolog's clause keys). An index selects
 * candidates on one position only: this checks every head argument (deep positions are not checked), before anything
 * costlier (unification, renaming) happens. One filter serves one iteration.
 * When candidates come from an index, it accepts the first one unchecked, and computes the query's keys when a second
 * one shows up, so that lookups with a single candidate (the common case, with a selective index) pay nothing for it.
 * When they come from a scan (small procedures, or no index usable), it checks them all.
 */
internal class KeyFilter private constructor(
    private val unificator: Unificator,
    private val head: Struct,
    // whether the first candidate was already selected by an index
    private var seen: Boolean,
) {
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

        /** A new filter for one iteration over the candidates for [query], [indexed] or not. */
        fun of(
            unificator: Unificator,
            query: Clause,
            indexed: Boolean,
        ): (Clause) -> Boolean = query.indexableHead()?.let { KeyFilter(unificator, it, !indexed)::accepts } ?: NONE
    }
}
