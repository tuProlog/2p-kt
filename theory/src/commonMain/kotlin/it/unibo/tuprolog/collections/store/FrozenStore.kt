package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

private val NO_POSITIONS = IntArray(0)

/**
 * The index of one argument of a [FrozenProcedure]: for each exact key, the positions of the clauses having that key
 * as argument, plus the positions of those whose argument has no exact key. All arrays are sorted. Immutable once
 * built: its fields being `val`s, it can be published to other threads through a data race.
 */
internal class FrozenArgIndex private constructor(
    val buckets: Map<IndexKey, IntArray>,
    val wildcards: IntArray,
) {
    companion object {
        fun build(
            clauses: Array<Clause>,
            argument: Int,
            unificator: Unificator,
        ): FrozenArgIndex? {
            val buckets = HashMap<IndexKey, MutableList<Int>>()
            val wildcards = mutableListOf<Int>()
            for (i in clauses.indices) {
                val key = unificator.indexKey(clauses[i].head!!.getArgAt(argument))
                if (key.isExact) buckets.getOrPut(key) { mutableListOf() }.add(i) else wildcards.add(i)
            }
            if (buckets.isEmpty()) return null
            return FrozenArgIndex(buckets.mapValues { it.value.toIntArray() }, wildcards.toIntArray())
        }
    }
}

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

/** Merges two sorted, disjoint arrays of positions. */
private class MergedPositions(
    private val first: IntArray,
    private val second: IntArray,
) : IntIterator() {
    private var i = 0
    private var j = 0

    override fun hasNext(): Boolean = i < first.size || j < second.size

    override fun nextInt(): Int =
        when {
            j >= second.size || (i < first.size && first[i] < second[j]) -> first[i++]
            else -> second[j++]
        }
}

/** Filters [candidates] down to the positions of clauses unifying with [query]. */
private class MatchingPositions(
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

private class PositionsToClauses(
    private val positions: IntIterator,
    private val clauses: Array<Clause>,
) : Iterator<Clause> {
    override fun hasNext(): Boolean = positions.hasNext()

    override fun next(): Clause = clauses[positions.nextInt()]
}

/**
 * An immutable, read-optimised clause store (#1019): clauses in an array (global order) and grouped into
 * [FrozenProcedure]s. Updates return new stores sharing every procedure they do not touch.
 */
internal class FrozenStore private constructor(
    val unificator: Unificator,
    val clauses: Array<Clause>,
    private val procedures: Map<ProcedureKey?, FrozenProcedure>,
) {
    val size: Int get() = clauses.size

    fun matching(query: Clause): Sequence<Clause> =
        procedures[procedureKeyOf(query)]?.matching(query) ?: emptySequence()

    /** A store with [added] (already validated) at the beginning or end. */
    fun plus(
        added: List<Clause>,
        atStart: Boolean = false,
    ): FrozenStore {
        if (added.isEmpty()) return this
        val newClauses = if (atStart) (added + clauses).toTypedArray() else (clauses.asList() + added).toTypedArray()
        val newProcedures = HashMap(procedures)
        for ((key, group) in added.groupBy { procedureKeyOf(it) }) {
            newProcedures[key] =
                procedures[key]?.plus(group, atStart) ?: FrozenProcedure(group.toTypedArray(), unificator)
        }
        return FrozenStore(unificator, newClauses, newProcedures)
    }

    /** The store without the first clause matching [query], and that clause; `null` if no clause matches. */
    fun minusFirst(query: Clause): Pair<FrozenStore, Clause>? {
        val key = procedureKeyOf(query)
        val procedure = procedures[key] ?: return null
        val matches = procedure.matches(query)
        if (!matches.hasNext()) return null
        val position = matches.nextInt()
        return minus(key, procedure, intArrayOf(position)) to procedure.clauses[position]
    }

    /** The store without the clauses matching [query], and those clauses; `null` if no clause matches. */
    fun minusAll(query: Clause): Pair<FrozenStore, List<Clause>>? {
        val key = procedureKeyOf(query)
        val procedure = procedures[key] ?: return null
        val positions = mutableListOf<Int>()
        procedure.matches(query).forEach { positions.add(it) }
        if (positions.isEmpty()) return null
        return minus(key, procedure, positions.toIntArray()) to positions.map { procedure.clauses[it] }
    }

    private fun minus(
        key: ProcedureKey?,
        procedure: FrozenProcedure,
        removed: IntArray,
    ): FrozenStore {
        val newClauses = ArrayList<Clause>(clauses.size - removed.size)
        // the procedure's clauses appear in the global array in the same order: walk both together
        var cursor = 0
        var next = 0
        for (clause in clauses) {
            if (cursor < procedure.size && clause === procedure.clauses[cursor]) {
                val isRemoved = next < removed.size && removed[next] == cursor
                cursor++
                if (isRemoved) {
                    next++
                    continue
                }
            }
            newClauses.add(clause)
        }
        val newProcedures = HashMap(procedures)
        if (procedure.size == removed.size) newProcedures.remove(key) else newProcedures[key] = procedure.minus(removed)
        return FrozenStore(unificator, newClauses.toTypedArray(), newProcedures)
    }

    companion object {
        fun empty(unificator: Unificator): FrozenStore = FrozenStore(unificator, emptyArray(), emptyMap())

        /** A store of [clauses], which must be already validated. */
        fun of(
            unificator: Unificator,
            clauses: List<Clause>,
        ): FrozenStore = empty(unificator).plus(clauses)
    }
}
