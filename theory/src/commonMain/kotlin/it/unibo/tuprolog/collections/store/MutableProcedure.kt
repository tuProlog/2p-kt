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

    // NotIndexable, a MutableArgIndex, or null if not built yet
    private var indexes: Array<Any?> = arrayOfNulls(arity)

    private fun keyOf(
        entry: Entry,
        argument: Int,
    ): IndexKey = unificator.indexKey(entry.clause.head!!.getArgAt(argument))

    fun add(
        entry: Entry,
        atStart: Boolean,
    ) {
        if (atStart) entries.addFirst(entry) else entries.addLast(entry)
        live++
        for (argument in indexes.indices) {
            val index = indexes[argument] ?: continue
            val key = keyOf(entry, argument)
            if (index === NotIndexable) {
                if (key.isExact) indexes[argument] = null
                continue
            }
            index as MutableArgIndex
            val deque = if (key.isExact) index.buckets.getOrPut(key) { EntryDeque() } else index.wildcards
            if (atStart) deque.addFirst(entry) else deque.addLast(entry)
        }
    }

    fun removed() {
        live--
        dead++
        if (dead > COMPACTION_SLACK && dead > live) {
            entries = EntryDeque.of(entries.alive())
            dead = 0
            indexes = arrayOfNulls(arity)
        }
    }

    private fun index(argument: Int): MutableArgIndex? {
        val cached = indexes[argument]
        if (cached != null) return if (cached === NotIndexable) null else cached as MutableArgIndex
        val buckets = HashMap<IndexKey, EntryDeque>()
        val wildcards = EntryDeque()
        for (entry in entries.alive()) {
            val key = keyOf(entry, argument)
            if (key.isExact) buckets.getOrPut(key) { EntryDeque() }.addLast(entry) else wildcards.addLast(entry)
        }
        if (buckets.isEmpty()) {
            indexes[argument] = NotIndexable
            return null
        }
        return MutableArgIndex(buckets, wildcards).also { indexes[argument] = it }
    }

    /** Views over the entries worth unifying with [query]: their merge, by order, holds all candidates. */
    fun candidates(query: Clause): Pair<EntryView, EntryView> {
        val head = query.indexableHead()
        if (head == null || live < INDEX_THRESHOLD) return entries.view() to EMPTY_VIEW
        var bestCost = entries.size
        var best: Pair<EntryView, EntryView>? = null
        for (argument in 0 until head.arity) {
            val key = unificator.indexKey(head.getArgAt(argument))
            if (!key.isExact) continue
            val index = index(argument) ?: continue
            val bucket = index.buckets[key]
            val cost = (bucket?.size ?: 0) + index.wildcards.size
            if (cost < bestCost) {
                bestCost = cost
                best = (bucket?.view() ?: EMPTY_VIEW) to index.wildcards.view()
                if (cost == 0) break
            }
        }
        return best ?: (entries.view() to EMPTY_VIEW)
    }

    private companion object {
        const val COMPACTION_SLACK = 8
    }
}
