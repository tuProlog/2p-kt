package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

internal val EMPTY_VIEW = EntryDeque().view()

/** The entries of one procedure of a [MutableStore], with argument indexes built on demand. */
internal class MutableProcedure(
    private val arity: Int,
    private val unificator: Unificator,
    var entries: EntryDeque = EntryDeque(),
) {
    var live: Int = 0
    var dead: Int = 0

    // per (argument, depth): NotIndexable, a MutableArgIndex, or null if not built yet
    private var indexes: Array<Any?> = arrayOfNulls(arity * MAX_INDEX_DEPTH)

    private fun keyOf(
        entry: Entry,
        slot: Int,
    ): IndexKey = unificator.nestedKey(entry.clause.head!!, slot / MAX_INDEX_DEPTH, slot % MAX_INDEX_DEPTH)

    fun add(
        entry: Entry,
        atStart: Boolean,
    ) {
        if (atStart) entries.addFirst(entry) else entries.addLast(entry)
        live++
        for (slot in indexes.indices) {
            val index = indexes[slot] ?: continue
            val key = keyOf(entry, slot)
            if (index === NotIndexable) {
                if (key.isExact) indexes[slot] = null
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
            indexes = arrayOfNulls(arity * MAX_INDEX_DEPTH)
            return
        }
        // buckets are compacted on their own too, or lookups in a hot bucket would skip up to `live` dead entries
        for (slot in indexes.indices) {
            val index = indexes[slot] as? MutableArgIndex ?: continue
            val key = keyOf(entry, slot)
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

    private fun index(slot: Int): MutableArgIndex? {
        val cached = indexes[slot]
        if (cached != null) return if (cached === NotIndexable) null else cached as MutableArgIndex
        val buckets = HashMap<IndexKey, EntryDeque>()
        val wildcards = EntryDeque()
        for (entry in entries.alive()) {
            val key = keyOf(entry, slot)
            if (key.isExact) buckets.getOrPut(key) { EntryDeque() }.addLast(entry) else wildcards.addLast(entry)
        }
        if (buckets.isEmpty()) {
            indexes[slot] = NotIndexable
            return null
        }
        return MutableArgIndex(buckets, wildcards).also { indexes[slot] = it }
    }

    /** Views over the entries worth unifying with [query]: their merge, by order, holds all candidates. */
    fun candidates(query: Clause): Pair<EntryView, EntryView> {
        val head = query.indexableHead()
        if (head == null || live < INDEX_THRESHOLD) return entries.view() to EMPTY_VIEW
        var bestCost = entries.size
        var best: Pair<EntryView, EntryView>? = null
        search@ for (argument in 0 until head.arity) {
            for (depth in 0 until MAX_INDEX_DEPTH) {
                val key = unificator.nestedKey(head, argument, depth)
                if (!key.isExact) break
                val index = index(argument * MAX_INDEX_DEPTH + depth) ?: continue
                val bucket = index.buckets[key]
                val cost = (bucket?.size ?: 0) + index.wildcards.size
                if (cost < bestCost) {
                    bestCost = cost
                    best = (bucket?.view() ?: EMPTY_VIEW) to index.wildcards.view()
                    if (cost == 0) break@search
                }
            }
        }
        return best ?: (entries.view() to EMPTY_VIEW)
    }

    private companion object {
        const val COMPACTION_SLACK = 8
    }
}
