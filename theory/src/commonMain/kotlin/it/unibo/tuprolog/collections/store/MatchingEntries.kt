package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

/** Merges two views sorted by [Entry.order], keeping the entries visible at [generation] and unifying with [query]. */
internal class MatchingEntries(
    private val first: EntryView,
    private val second: EntryView,
    private val generation: Long,
    private val query: Clause,
    private val unificator: Unificator,
) : Iterator<Entry> {
    private var i = 0
    private var j = 0
    private var next: Entry? = null

    override fun hasNext(): Boolean {
        while (next == null && (i < first.size || j < second.size)) {
            val candidate =
                when {
                    j >= second.size || (i < first.size && first[i].order < second[j].order) -> first[i++]
                    else -> second[j++]
                }
            if (candidate.isVisibleAt(generation) && unificator.match(query, candidate.clause)) next = candidate
        }
        return next != null
    }

    override fun next(): Entry {
        if (!hasNext()) throw NoSuchElementException()
        return next!!.also { next = null }
    }
}
