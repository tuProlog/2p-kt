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
    private val arity: Int = clauses.firstOrNull()?.head?.arity ?: 0

    // NotIndexable, a FrozenArgIndex, or null if not built yet; races only ever store equivalent values
    private val indexes: Array<Any?> = arrayOfNulls(arity)

    val size: Int get() = clauses.size

    private fun index(argument: Int): FrozenArgIndex? {
        val cached = indexes[argument]
        if (cached != null) return if (cached === NotIndexable) null else cached as FrozenArgIndex
        val built = FrozenArgIndex.build(clauses, argument, unificator)
        indexes[argument] = built ?: NotIndexable
        return built
    }

    /** Positions of the clauses worth unifying with [query], in order. */
    fun candidates(query: Clause): IntIterator {
        val head = query.indexableHead()
        if (head == null || clauses.size < INDEX_THRESHOLD) return (clauses.indices).iterator()
        var bestCost = clauses.size
        var bestBucket: IntArray? = null
        var bestWildcards: IntArray? = null
        for (argument in 0 until head.arity) {
            val key = unificator.indexKey(head.getArgAt(argument))
            if (!key.isExact) continue
            val index = index(argument) ?: continue
            val bucket = index.buckets[key] ?: NO_POSITIONS
            val cost = bucket.size + index.wildcards.size
            if (cost < bestCost) {
                bestCost = cost
                bestBucket = bucket
                bestWildcards = index.wildcards
                if (cost == 0) break
            }
        }
        return if (bestBucket == null) clauses.indices.iterator() else MergedPositions(bestBucket, bestWildcards!!)
    }

    /** Positions of the clauses unifying with [query], in order. */
    fun matches(query: Clause): IntIterator = MatchingPositions(candidates(query), clauses, query, unificator)

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
