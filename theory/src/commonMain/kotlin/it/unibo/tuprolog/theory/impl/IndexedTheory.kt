package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.collections.store.FrozenStore
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.RetractResult
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.TheoryUtils.checkClauseCorrect
import it.unibo.tuprolog.theory.validated
import it.unibo.tuprolog.unify.Unificator

/**
 * The immutable, read-optimised [Theory], backed by a [FrozenStore]: clause lookups go through argument
 * indexes built on demand; updates return new theories sharing every procedure they do not touch.
 */
internal class IndexedTheory private constructor(
    private val store: FrozenStore,
    tags: Map<String, Any>,
) : AbstractTheory(tags) {
    /** Builds a theory from the given clauses, after checking they are well formed. */
    constructor(
        unificator: Unificator,
        clauses: Iterable<Clause>,
        tags: Map<String, Any> = emptyMap(),
    ) : this(FrozenStore.of(unificator, clauses.validated()), tags)

    /** Builds a theory from the given clauses, after checking they are well formed. */
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

    override val clauses: List<Clause> = store.clauses.asList()

    override fun iterator(): Iterator<Clause> = clauses.iterator()

    override val size: Long get() = store.size.toLong()

    override val isEmpty: Boolean get() = store.size == 0

    override val isNonEmpty: Boolean get() = store.size > 0

    override fun get(clause: Clause): Sequence<Clause> = store.matching(clause)

    override fun candidates(query: Clause): Sequence<Clause> = store.candidates(query)

    override fun createNewTheory(
        clauses: Sequence<Clause>,
        tags: Map<String, Any>,
        unificator: Unificator,
    ): IndexedTheory = IndexedTheory(unificator, clauses, tags)

    private fun with(store: FrozenStore): IndexedTheory = if (store === this.store) this else IndexedTheory(store, tags)

    override fun assertA(clause: Clause): Theory = with(store.plus(listOf(checkClauseCorrect(clause)), atStart = true))

    override fun assertA(clauses: Iterable<Clause>): Theory = with(store.plus(clauses.validated(), atStart = true))

    override fun assertA(clauses: Sequence<Clause>): Theory = assertA(clauses.asIterable())

    override fun assertZ(clause: Clause): Theory = with(store.plus(listOf(checkClauseCorrect(clause))))

    override fun assertZ(clauses: Iterable<Clause>): Theory = with(store.plus(clauses.validated()))

    override fun assertZ(clauses: Sequence<Clause>): Theory = assertZ(clauses.asIterable())

    override fun plus(clause: Clause): Theory = assertZ(clause)

    // not `theory` itself when this is empty: the result keeps this theory's unificator and tags
    override fun plus(theory: Theory): Theory = assertZ(theory.clauses)

    override fun retract(clause: Clause): RetractResult<IndexedTheory> =
        store.minusFirst(clause)?.let { (store, removed) -> RetractResult.Success(with(store), listOf(removed)) }
            ?: RetractResult.Failure(this)

    override fun retract(clauses: Iterable<Clause>): RetractResult<IndexedTheory> {
        var current = store
        val removed = mutableListOf<Clause>()
        for (clause in clauses) {
            current.minusFirst(clause)?.let { (store, clause) ->
                current = store
                removed.add(clause)
            }
        }
        return if (removed.isEmpty()) RetractResult.Failure(this) else RetractResult.Success(with(current), removed)
    }

    override fun retractAll(clause: Clause): RetractResult<IndexedTheory> =
        store.minusAll(clause)?.let { (store, removed) -> RetractResult.Success(with(store), removed) }
            ?: RetractResult.Failure(this)

    override fun firstOrNull(): Clause? = clauses.firstOrNull()

    override fun lastOrNull(): Clause? = clauses.lastOrNull()

    override fun toMutableTheory(): MutableTheory = MutableIndexedTheory(unificator, clauses, tags)

    // cached like java.lang.String's: racy but safe, as each field is either unset or final, with no lazy holder to read
    private var hash = 0
    private var hashIsZero = false

    override fun hashCode(): Int {
        var h = hash
        if (h == 0 && !hashIsZero) {
            h = super.hashCode()
            if (h == 0) hashIsZero = true else hash = h
        }
        return h
    }

    override fun replaceTags(tags: Map<String, Any>): IndexedTheory =
        if (tags === this.tags) this else IndexedTheory(store, tags)
}
