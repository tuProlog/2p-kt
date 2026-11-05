package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause

/** Filters [candidates] down to the positions of clauses satisfying [accept]. */
internal class MatchingPositions(
    private val candidates: IntIterator,
    private val clauses: Array<Clause>,
    private val accept: (Clause) -> Boolean,
) : IntIterator() {
    private var next = -1

    override fun hasNext(): Boolean {
        while (next < 0 && candidates.hasNext()) {
            val candidate = candidates.nextInt()
            if (accept(clauses[candidate])) next = candidate
        }
        return next >= 0
    }

    override fun nextInt(): Int {
        if (!hasNext()) throw NoSuchElementException()
        return next.also { next = -1 }
    }
}
