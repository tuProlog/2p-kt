package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal val NO_POSITIONS = IntArray(0)

/**
 * The clauses of one procedure of a [FrozenStore], in order, with their argument indexes built on demand.
 * Updating a store copies the procedures it touches, and shares (with their indexes) all the others.
 */
internal class FrozenProcedure(
    val clauses: Array<Clause>,
    private val unificator: Unificator,
) {
    private var indexes = FrozenIndexes(emptyMap())

    val size: Int get() = clauses.size

    private fun index(path: Int): FrozenArgIndex? {
        val cached = indexes.byPath[path]
        if (cached != null) return if (cached === NotIndexable) null else cached as FrozenArgIndex
        val built = FrozenArgIndex.build(clauses, path, unificator)
        indexes = FrozenIndexes(indexes.byPath + (path to (built ?: NotIndexable)))
        return built
    }

    /** Positions of the clauses worth unifying with [query], in order. */
    fun candidates(query: Clause): IntIterator {
        val head = query.indexableHead()
        if (head == null || clauses.size < INDEX_THRESHOLD) return (clauses.indices).iterator()
        var bestCost = clauses.size
        var bestBucket: IntArray? = null
        var bestWildcards: IntArray? = null
        unificator.searchPositions(head) { path, key ->
            val index = index(path) ?: return@searchPositions Visit.SKIP
            val bucket = index.buckets[key] ?: NO_POSITIONS
            val cost = bucket.size + index.wildcards.size
            if (cost < bestCost) {
                bestCost = cost
                bestBucket = bucket
                bestWildcards = index.wildcards
            }
            // deeper positions only help where this one leaves several clauses with the query's key
            when {
                cost == 0 -> Visit.STOP
                bucket.size > 1 -> Visit.DESCEND
                else -> Visit.SKIP
            }
        }
        val bucket = bestBucket ?: return clauses.indices.iterator()
        return MergedPositions(bucket, bestWildcards!!)
    }

    /** Positions of the clauses unifying with [query], in order. */
    fun matches(query: Clause): IntIterator {
        val candidates = candidates(query)
        if (!candidates.hasNext()) return candidates
        val keys = KeyFilter.of(unificator, query, candidates is MergedPositions)
        return MatchingPositions(candidates, clauses) { keys(it) && unificator.match(query, it) }
    }

    /** The clauses whose keys agree with [query]'s, in order: a superset of those unifying with it. */
    fun keyMatching(query: Clause): Sequence<Clause> =
        Sequence {
            val candidates = candidates(query)
            val keys =
                if (candidates.hasNext()) {
                    KeyFilter.of(
                        unificator,
                        query,
                        candidates is MergedPositions,
                    )
                } else {
                    null
                }
            PositionsToClauses(if (keys == null) candidates else MatchingPositions(candidates, clauses, keys), clauses)
        }

    fun matching(query: Clause): Sequence<Clause> = Sequence { PositionsToClauses(matches(query), clauses) }

    /** A copy of this procedure with [added] at its beginning or end. */
    fun plus(
        added: List<Clause>,
        atStart: Boolean,
    ): FrozenProcedure =
        FrozenProcedure(
            if (atStart) (added + clauses).toTypedArray() else (clauses.asList() + added).toTypedArray(),
            unificator,
        )

    /** A copy of this procedure without the clauses at the given (sorted) positions. */
    fun minus(removed: IntArray): FrozenProcedure {
        var next = 0
        return FrozenProcedure(
            clauses
                .filterIndexed { i, _ -> !(next < removed.size && removed[next] == i).also { if (it) next++ } }
                .toTypedArray(),
            unificator,
        )
    }
}
