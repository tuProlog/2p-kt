package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

internal val EMPTY_VIEW = EntryDeque().view()

/** The entries of one procedure of a [MutableStore], with argument indexes built on demand. */
internal class MutableProcedure(
    private val unificator: Unificator,
    var entries: EntryDeque = EntryDeque(),
) {
    var live: Int = 0
    var dead: Int = 0

    // per path (see keyAt): NotIndexable or a MutableArgIndex, once built; the lists below hold the same, for updates
    private val indexes = HashMap<Int, Any>()
    private val built = ArrayList<MutableArgIndex>()
    private val unindexable = ArrayList<Int>()

    private fun keyOf(
        entry: Entry,
        steps: IntArray,
    ): IndexKey = unificator.keyAt(entry.clause.concreteHead()!!, steps)

    fun add(
        entry: Entry,
        atStart: Boolean,
    ) {
        if (atStart) entries.addFirst(entry) else entries.addLast(entry)
        live++
        for (i in built.indices) {
            val index = built[i]
            val key = keyOf(entry, index.steps)
            val deque = if (key.isExact) index.buckets.getOrPut(key) { EntryDeque() } else index.wildcards
            if (atStart) deque.addFirst(entry) else deque.addLast(entry)
        }
        // a position with no exact key so far may have one now: build its index again on the next lookup needing it
        var i = 0
        while (i < unindexable.size) {
            val path = unindexable[i]
            if (keyOf(entry, stepsOf(path)).isExact) {
                indexes.remove(path)
                unindexable[i] = unindexable[unindexable.lastIndex]
                unindexable.removeAt(unindexable.lastIndex)
            } else {
                i++
            }
        }
    }

    /** Records that [entry], one of this procedure's, was just removed. */
    fun removed(entry: Entry) {
        live--
        dead++
        if (dead > COMPACTION_SLACK && dead > live) {
            entries = EntryDeque.of(entries.alive())
            dead = 0
        }
        // indexes are kept, compacting each bucket on its own: rebuilding them all would cost O(n) every n removals
        for (i in built.indices) {
            val index = built[i]
            val key = keyOf(entry, index.steps)
            val deque = (if (key.isExact) index.buckets[key] else index.wildcards) ?: continue
            deque.dead++
            if (key.isExact && deque.dead == deque.size) {
                index.buckets.remove(key)
                continue
            }
            if (deque.dead <= COMPACTION_SLACK || 2 * deque.dead <= deque.size) continue
            val compacted = EntryDeque.of(deque.alive())
            when {
                !key.isExact -> index.wildcards = compacted
                compacted.size == 0 -> index.buckets.remove(key)
                else -> index.buckets[key] = compacted
            }
        }
    }

    private fun index(path: Int): MutableArgIndex? {
        val cached = indexes[path]
        if (cached != null) return if (cached === NotIndexable) null else cached as MutableArgIndex
        val buckets = HashMap<IndexKey, EntryDeque>()
        val wildcards = EntryDeque()
        val steps = stepsOf(path)
        for (entry in entries.alive()) {
            val key = keyOf(entry, steps)
            if (key.isExact) buckets.getOrPut(key) { EntryDeque() }.addLast(entry) else wildcards.addLast(entry)
        }
        if (buckets.isEmpty()) {
            indexes[path] = NotIndexable
            unindexable.add(path)
            return null
        }
        return MutableArgIndex(path, steps, buckets, wildcards).also {
            indexes[path] = it
            built.add(it)
        }
    }

    /** Views over the entries worth unifying with [query]: their merge, by order, holds all candidates. */
    fun candidates(query: Clause): Pair<EntryView, EntryView> {
        val head = query.indexableHead()
        if (head == null || live < INDEX_THRESHOLD) return entries.view() to EMPTY_VIEW
        var bestCost = entries.size
        var best: Pair<EntryView, EntryView>? = null
        unificator.searchPositions(head) { path, key ->
            val index = index(path) ?: return@searchPositions Visit.SKIP
            val bucket = index.buckets[key]
            val bucketSize = bucket?.size ?: 0
            val cost = bucketSize + index.wildcards.size
            if (cost < bestCost) {
                bestCost = cost
                best = (bucket?.view() ?: EMPTY_VIEW) to index.wildcards.view()
            }
            // deeper positions only help where this one leaves several clauses with the query's key
            when {
                cost == 0 -> Visit.STOP
                bucketSize > 1 -> Visit.DESCEND
                else -> Visit.SKIP
            }
        }
        return best ?: (entries.view() to EMPTY_VIEW)
    }

    private companion object {
        const val COMPACTION_SLACK = 8
    }
}
