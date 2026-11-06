package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
/*
 * A write-optimised clause store, implementing the logical update view (ISO/IEC 13211-1, 7.5.4) through generations
 * (T. Lindholm, R. A. O'Keefe, Efficient Implementation of a Defensible Semantics for Dynamic Prolog Code, ICLP 1987): every update increments the store's generation; an entry records the generation it was added
 * in and the one it was removed in; a lookup only sees the entries alive at the generation current when it started.
 * Removal only marks entries, which are physically dropped later, when they outnumber the live ones.
 *
 * Lookups capture (array, from, to) views of the deques they read. Deques only ever write outside the range of
 * existing views (they grow at both ends, and compaction or growth allocates a new array), so lookups are lazy and
 * stay valid while the store changes, without copying anything.
 */

/** A mutable, write-optimised clause store: see the comment at the top of this file. */
internal class MutableStore(
    override val unificator: Unificator,
) : ClauseSource {
    private var generation: Long = 0
    private var lowestOrder: Long = 0
    private var highestOrder: Long = 0
    private var all = EntryDeque()
    private var dead = 0
    private var customHeadKeys = 0
    private val procedures = HashMap<ProcedureKey?, MutableProcedure>()

    /** Number of live clauses. */
    override var size: Int = 0
        private set

    /** Adds [clause] (already validated) at the beginning or end. */
    fun add(
        clause: Clause,
        atStart: Boolean = false,
    ) {
        val entry = Entry(clause, if (atStart) --lowestOrder else ++highestOrder, ++generation)
        if (atStart) all.addFirst(entry) else all.addLast(entry)
        val key = procedureKeyOf(clause)
        procedures.getOrPut(key) { MutableProcedure(unificator) }.add(entry, atStart)
        size++
        if (unificator.hasCustomHeadKey(clause)) customHeadKeys++
    }

    fun addAll(
        clauses: List<Clause>,
        atStart: Boolean = false,
    ) {
        if (atStart) {
            for (i in clauses.indices.reversed()) add(clauses[i], true)
        } else {
            for (clause in clauses) add(clause)
        }
    }

    /** The entries whose keys agree with [query]'s, and whose clause satisfies [accept], as of now, lazily. */
    private fun entries(
        query: Clause,
        accept: (Clause) -> Boolean,
    ): Sequence<Entry> {
        if (customHeadKeys > 0 || unificator.hasCustomHeadKey(query)) {
            val view = all.view()
            val generation = generation
            return Sequence { matchingEntries(view, EMPTY_VIEW, generation, accept) }
        }
        val procedure = procedures[procedureKeyOf(query)] ?: return emptySequence()
        val (first, second) = procedure.candidates(query)
        if (first.size == 0 && second.size == 0) return emptySequence()
        val generation = generation
        return Sequence {
            // MutableProcedure.candidates gives EMPTY_VIEW as second view only when it scans the whole procedure
            val keys = KeyFilter.of(unificator, query, indexed = second !== EMPTY_VIEW)
            // no key to check (e.g. zero-arity heads): pass accept as is, sparing a lambda per clause
            val test = if (keys === KeyFilter.NONE) accept else { clause -> keys(clause) && accept(clause) }
            matchingEntries(first, second, generation, test)
        }
    }

    /** The entries matching [query], as of now, lazily. */
    private fun matchingEntries(query: Clause): Sequence<Entry> = entries(query) { unificator.match(query, it) }

    /** The clauses matching [query] as of this call (logical update view), lazily. */
    override fun matching(query: Clause): Sequence<Clause> = matchingEntries(query).map { it.clause }

    override fun candidates(query: Clause): Sequence<Clause> = entries(query) { true }.map { it.clause }

    /** All live entries, in order, as of the call to [Sequence.iterator]. */
    fun entries(): Sequence<Entry> = Sequence { all.view().visibleAt(generation) { it } }

    override fun ordered(): Sequence<Clause> = Sequence { all.view().visibleAt(generation) { it.clause } }

    /** The last live entry, or `null` if empty. */
    fun lastEntry(): Entry? = all.view().asReversedSequence().firstOrNull { it.isAlive }

    /** Removes all clauses. */
    fun clear() {
        entries().toList().forEach { remove(it) }
    }

    /** Removes [entry], which must be alive. */
    fun remove(entry: Entry) {
        entry.erased = ++generation
        size--
        if (unificator.hasCustomHeadKey(entry.clause)) customHeadKeys--
        dead++
        val key = procedureKeyOf(entry.clause)
        val procedure = procedures[key]!!
        procedure.removed(entry)
        if (procedure.live == 0) procedures.remove(key)
        if (dead > COMPACTION_SLACK && dead > size) {
            all = EntryDeque.of(all.alive())
            dead = 0
        }
    }

    /**
     * Removes the first clause matching [query] (and satisfying [filter]), returning it, or `null` if none matches.
     */
    fun removeFirst(
        query: Clause,
        filter: (Clause) -> Boolean = { true },
    ): Clause? = matchingEntries(query).firstOrNull { filter(it.clause) }?.also { remove(it) }?.clause

    /** Removes all clauses matching [query], returning them (empty if none matches). */
    fun removeAll(query: Clause): List<Clause> = matchingEntries(query).toList().onEach { remove(it) }.map { it.clause }

    private companion object {
        const val COMPACTION_SLACK = 16
    }
}
