package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

/** Filters [candidates] down to the positions of clauses unifying with [query]. */
internal class MatchingPositions(
    private val candidates: IntIterator,
    private val clauses: Array<Clause>,
    private val query: Clause,
    private val unificator: Unificator,
) : IntIterator() {
    private var next = -1

    override fun hasNext(): Boolean {
        while (next < 0 && candidates.hasNext()) {
            val candidate = candidates.nextInt()
            if (unificator.match(query, clauses[candidate])) next = candidate
        }
        return next >= 0
    }

    override fun nextInt(): Int {
        if (!hasNext()) throw NoSuchElementException()
        return next.also { next = -1 }
    }
}
