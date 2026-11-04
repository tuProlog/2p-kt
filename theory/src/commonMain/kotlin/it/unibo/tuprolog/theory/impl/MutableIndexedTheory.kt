package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.store.MutableStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClauseCorrect
import it.unibo.tuprolog.theory.validated
import it.unibo.tuprolog.unify.Unificator

/**
 * The mutable, write-optimised [MutableTheory] (#1019), backed by a [MutableStore]: assertions and retractions are
 * amortised O(1) after lookup, and lookups and iterations follow the logical update view: they see the clauses as of
 * when they started, whatever happens to the theory while they are consumed.
 */
internal class MutableIndexedTheory private constructor(
    private val store: MutableStore,
    tags: Map<String, Any>,
) : AbstractTheory(tags),
    MutableTheory {
    /** Construct a Clause database from given clauses */
    constructor(
        unificator: Unificator,
        clauses: Iterable<Clause>,
        tags: Map<String, Any> = emptyMap(),
    ) : this(MutableStore(unificator).also { it.addAll(clauses.validated()) }, tags)

    /** Construct a Clause database from given clauses */
    constructor(
        unificator: Unificator,
        clauses: Sequence<Clause>,
        tags: Map<String, Any> = emptyMap(),
    ) : this(unificator, clauses.asIterable(), tags)

    @Suppress("UNUSED_PARAMETER")
    override var unificator: Unificator
        get() = store.unificator
        set(value) {
            error("Indexed theories do not support changing the unification without rebuilding")
        }

    override fun setUnificator(unificator: Unificator): MutableTheory =
        if (unificator == this.unificator) this else MutableIndexedTheory(unificator, clauses, tags)

    override fun toMutableTheory(): MutableTheory = this

    override val clauses: Iterable<Clause> = Iterable { store.clauses().iterator() }

    override fun iterator(): Iterator<Clause> = clauses.iterator()

    override val size: Long get() = store.size.toLong()

    override val isEmpty: Boolean get() = store.size == 0

    override val isNonEmpty: Boolean get() = store.size > 0

    override fun get(clause: Clause): Sequence<Clause> = store.matching(clause)

    override fun createNewTheory(
        clauses: Sequence<Clause>,
        tags: Map<String, Any>,
        unificator: Unificator,
    ): MutableIndexedTheory = MutableIndexedTheory(unificator, clauses, tags)

    override fun retract(clause: Clause): RetractResult<MutableIndexedTheory> =
        store.removeFirst(clause)?.let { RetractResult.Success(this, listOf(it)) } ?: RetractResult.Failure(this)

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableIndexedTheory> {
        val removed = clauses.mapNotNull { store.removeFirst(it) }
        return if (removed.isEmpty()) RetractResult.Failure(this) else RetractResult.Success(this, removed)
    }

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableIndexedTheory> = retract(clauses.asIterable())

    override fun retractAll(clause: Clause): RetractResult<MutableIndexedTheory> =
        store
            .removeAll(
                clause,
            ).let { if (it.isEmpty()) RetractResult.Failure(this) else RetractResult.Success(this, it) }

    override fun plus(clause: Clause): MutableIndexedTheory = assertZ(clause)

    override fun plus(theory: Theory): MutableIndexedTheory = assertZ(theory.clauses)

    override fun assertA(clause: Clause): MutableIndexedTheory =
        also {
            store.add(checkClauseCorrect(clause), atStart = true)
        }

    override fun assertA(clauses: Iterable<Clause>): MutableIndexedTheory =
        also {
            store.addAll(clauses.validated(), atStart = true)
        }

    override fun assertA(clauses: Sequence<Clause>): MutableIndexedTheory = assertA(clauses.asIterable())

    override fun assertZ(clause: Clause): MutableIndexedTheory = also { store.add(checkClauseCorrect(clause)) }

    override fun assertZ(clauses: Iterable<Clause>): MutableIndexedTheory = also { store.addAll(clauses.validated()) }

    override fun assertZ(clauses: Sequence<Clause>): MutableIndexedTheory = assertZ(clauses.asIterable())

    override fun abolish(indicator: Indicator): MutableTheory = also { super.abolish(indicator) }

    override fun toImmutableTheory(): Theory = IndexedTheory(unificator, clauses, tags)

    override fun replaceTags(tags: Map<String, Any>): MutableIndexedTheory =
        if (tags === this.tags) this else MutableIndexedTheory(store, tags)

    override fun clone(): MutableTheory = MutableIndexedTheory(unificator, clauses, tags)
}
