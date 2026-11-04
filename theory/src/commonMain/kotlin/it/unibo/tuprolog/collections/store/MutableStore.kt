package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.IndexKey
import it.unibo.tuprolog.unify.Unificator

/*
 * A write-optimised clause store (#1019), implementing the logical update view through generations (Lindholm and
 * O'Keefe, ICLP 1987): every update increments the store's generation; an entry records the generation it was added
 * in and the one it was removed in; a lookup only sees the entries alive at the generation current when it started.
 * Removal only marks entries, which are physically dropped later, when they outnumber the live ones.
 *
 * Lookups capture (array, from, to) views of the deques they read. Deques only ever write outside the range of
 * existing views (they grow at both ends, and compaction or growth allocates a new array), so lookups are lazy and
 * stay valid while the store changes, without copying anything.
 */

/** A clause in a [MutableStore], with its position in the global order and its lifetime. */
internal class Entry(
    val clause: Clause,
    val order: Long,
    val born: Long,
) {
    var erased: Long = Long.MAX_VALUE

    val isAlive: Boolean get() = erased == Long.MAX_VALUE

    fun isVisibleAt(generation: Long): Boolean = born <= generation && generation < erased
}

/** An array-backed deque of [Entry]s, appending in place at both ends. */
internal class EntryDeque private constructor(
    private var array: Array<Entry?>,
    private var head: Int,
) {
    private var tail: Int = head

    constructor() : this(arrayOfNulls(INITIAL_CAPACITY), INITIAL_CAPACITY / 2)

    val size: Int get() = tail - head

    fun addLast(entry: Entry) {
        if (tail == array.size) grow()
        array[tail++] = entry
    }

    fun addFirst(entry: Entry) {
        if (head == 0) grow()
        array[--head] = entry
    }

    private fun grow() {
        val size = size
        val capacity = 2 * size + INITIAL_CAPACITY
        val newHead = (capacity - size) / 2
        val newArray = arrayOfNulls<Entry>(capacity)
        array.copyInto(newArray, newHead, head, tail)
        array = newArray
        head = newHead
        tail = newHead + size
    }

    fun view(): EntryView = EntryView(array, head, tail)

    fun alive(): Sequence<Entry> = view().asSequence().filter { it.isAlive }

    companion object {
        private const val INITIAL_CAPACITY = 8

        fun of(entries: Sequence<Entry>): EntryDeque =
            EntryDeque().also { deque -> entries.forEach { deque.addLast(it) } }
    }
}

/** A snapshot of the content of an [EntryDeque]. */
internal class EntryView(
    private val array: Array<Entry?>,
    private val from: Int,
    private val to: Int,
) {
    val size: Int get() = to - from

    operator fun get(i: Int): Entry = array[from + i]!!

    fun asSequence(): Sequence<Entry> = (from until to).asSequence().map { array[it]!! }

    fun asReversedSequence(): Sequence<Entry> = (to - 1 downTo from).asSequence().map { array[it]!! }
}

private val EMPTY_VIEW = EntryDeque().view()

/** An argument index of a [MutableProcedure], maintained on insertion once built. */
internal class MutableArgIndex(
    val buckets: HashMap<IndexKey, EntryDeque>,
    val wildcards: EntryDeque,
)

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

/** Merges two views sorted by [Entry.order], keeping the entries visible at [generation] and unifying with [query]. */
private class MatchingEntries(
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

/** A mutable, write-optimised clause store: see the comment at the top of this file. */
internal class MutableStore(
    override val unificator: Unificator,
) : ClauseSource {
    private var generation: Long = 0
    private var lowestOrder: Long = 0
    private var highestOrder: Long = 0
    private var all = EntryDeque()
    private var dead = 0
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
        procedures.getOrPut(key) { MutableProcedure(key?.arity ?: 0, unificator) }.add(entry, atStart)
        size++
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

    /** The entries matching [query], as of now, lazily. */
    private fun matchingEntries(query: Clause): Sequence<Entry> {
        val procedure = procedures[procedureKeyOf(query)] ?: return emptySequence()
        val (first, second) = procedure.candidates(query)
        val generation = generation
        return Sequence { MatchingEntries(first, second, generation, query, unificator) }
    }

    /** The clauses matching [query] as of this call (logical update view), lazily. */
    override fun matching(query: Clause): Sequence<Clause> = matchingEntries(query).map { it.clause }

    /** All live entries, in order, as of the call to [Sequence.iterator]. */
    fun entries(): Sequence<Entry> =
        Sequence {
            val generation = generation
            all
                .view()
                .asSequence()
                .filter { it.isVisibleAt(generation) }
                .iterator()
        }

    override fun ordered(): Sequence<Clause> = entries().map { it.clause }

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
        dead++
        val key = procedureKeyOf(entry.clause)
        val procedure = procedures[key]!!
        procedure.removed()
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
