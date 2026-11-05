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

    // per path (see keyAt): NotIndexable or a MutableArgIndex, once built
    private val indexes = HashMap<Int, Any>()

    private fun keyOf(
        entry: Entry,
        path: Int,
    ): IndexKey = unificator.keyAt(entry.clause.head!!, path)

    fun add(
        entry: Entry,
        atStart: Boolean,
    ) {
        if (atStart) entries.addFirst(entry) else entries.addLast(entry)
        live++
        val slots = indexes.entries.iterator()
        while (slots.hasNext()) {
            val (path, index) = slots.next()
            val key = keyOf(entry, path)
            if (index === NotIndexable) {
                if (key.isExact) slots.remove()
                continue
            }
            index as MutableArgIndex
            val deque = if (key.isExact) index.buckets.getOrPut(key) { EntryDeque() } else index.wildcards
            if (atStart) deque.addFirst(entry) else deque.addLast(entry)
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
        for ((path, slot) in indexes) {
            val index = slot as? MutableArgIndex ?: continue
            val key = keyOf(entry, path)
            val deque = (if (key.isExact) index.buckets[key] else index.wildcards) ?: continue
            if (++deque.dead <= COMPACTION_SLACK || 2 * deque.dead <= deque.size) continue
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
        for (entry in entries.alive()) {
            val key = keyOf(entry, path)
            if (key.isExact) buckets.getOrPut(key) { EntryDeque() }.addLast(entry) else wildcards.addLast(entry)
        }
        if (buckets.isEmpty()) {
            indexes[path] = NotIndexable
            return null
        }
        return MutableArgIndex(buckets, wildcards).also { indexes[path] = it }
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
