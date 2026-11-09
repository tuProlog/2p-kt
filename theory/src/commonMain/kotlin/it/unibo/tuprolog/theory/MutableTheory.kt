package it.unibo.tuprolog.theory

import it.unibo.tuprolog.collections.MutableClauseDeque
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.theory.impl.MutableIndexedTheory
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Theory] that mutates itself in place: `assertA`/`assertZ`/`retract`/`abolish` (and their `plus` shorthands)
 * change and return `this`, instead of building a fresh [Theory]. This is cheaper than the persistent-data-
 * structure behaviour of a plain [Theory] when clauses are asserted/retracted very frequently — e.g. to back a
 * Prolog `dynamic` knowledge base updated on (almost) every resolution step — at the cost of the usual aliasing
 * caveat: every reference to a `MutableTheory` observes every mutation performed through any other reference.
 *
 * A `MutableTheory` is a [MutableClauseDeque]: `add`/`addLast` mean `assertZ`, `addFirst` means `assertA`,
 * `removeFirstMatching` means `retract`. [Theory.toMutableTheory] and [Theory.toImmutableTheory] convert between
 * the two flavours: they return `this` when it already has the requested flavour, and copy the clauses otherwise.
 * [setUnificator] (with a different unificator) and [clone] also return a copy, as they change how clauses are indexed.
 *
 * Lookups ([get], [candidates]) and iterations ([clauses]) follow the *logical update view* (ISO/IEC 13211-1, 7.5.4):
 * they see the clauses as of when they started (the call to [get], or to `iterator()`), whatever assertions and
 * retractions happen while they are consumed. There is therefore no need to copy their results before updating the
 * theory. This is implemented with generation counters (every clause records the generations it was added and removed
 * in, and a lookup only yields the clauses alive at the generation it started at), after Lindholm and O'Keefe.
 * Retracted clauses are reclaimed once they outnumber the live ones, so storage stays proportional to the live clauses.
 *
 * References:
 * - T. Lindholm, R. A. O'Keefe. *Efficient Implementation of a Defensible Semantics for Dynamic Prolog Code*.
 *   ICLP 1987, MIT Press.
 * - ISO/IEC 13211-1:1995, *Information technology — Programming languages — Prolog — Part 1: General core*, 7.5.4.
 *
 * @see Theory
 */
interface MutableTheory :
    Theory,
    MutableClauseDeque {
    /** A [MutableTheory] using [unificator]: `this`, if it equals the current one, a new (independent) copy otherwise. */
    override fun setUnificator(unificator: Unificator): MutableTheory

    /** An independent copy of this theory, like [clone]. */
    override fun toMutable(): MutableTheory = clone()

    override fun add(clause: Clause): Boolean {
        assertZ(clause)
        return true
    }

    override fun addAll(clauses: Iterable<Clause>): Boolean = clauses.toList().also { assertZ(it) }.isNotEmpty()

    override fun addFirst(clause: Clause) {
        assertA(clause)
    }

    override fun addLast(clause: Clause) {
        assertZ(clause)
    }

    override fun removeFirstMatching(query: Clause): Clause? = retract(query).firstClause

    override fun removeAllMatching(query: Clause): List<Clause> = retractAll(query).clauses?.toList() ?: emptyList()

    // the first clause is the first one matching itself
    override fun removeFirst(): Clause? = firstOrNull()?.also { retract(it) }

    override fun removeLast(): Clause? {
        val all = clauses.toList()
        clear()
        assertZ(all.dropLast(1))
        return all.lastOrNull()
    }

    // retracting each clause in order removes exactly that clause, as earlier ones are gone
    override fun clear() {
        retract(clauses.toList())
    }

    override val isMutable: Boolean get() = true

    override fun toMutableTheory(): MutableTheory = this

    /** Appends the clauses of [theory] to this one, in place, and returns `this`; equivalent to `assertZ(theory.clauses)`. */
    override operator fun plus(theory: Theory): MutableTheory

    override operator fun plus(clause: Clause): MutableTheory = assertZ(clause)

    override fun assertA(clause: Clause): MutableTheory

    override fun assertA(struct: Struct): MutableTheory = assertA(Fact.of(struct))

    override fun assertA(clauses: Iterable<Clause>): MutableTheory

    override fun assertA(clauses: Sequence<Clause>): MutableTheory

    override fun assertZ(clause: Clause): MutableTheory

    override fun assertZ(struct: Struct): MutableTheory = assertZ(Fact.of(struct))

    override fun assertZ(clauses: Iterable<Clause>): MutableTheory

    override fun assertZ(clauses: Sequence<Clause>): MutableTheory

    override fun retract(clause: Clause): RetractResult<MutableTheory>

    override fun retract(clauses: Iterable<Clause>): RetractResult<MutableTheory>

    override fun retract(clauses: Sequence<Clause>): RetractResult<MutableTheory>

    override fun retract(head: Struct): RetractResult<MutableTheory> = retract(Rule.of(head, Var.anonymous()))

    override fun retractAll(clause: Clause): RetractResult<MutableTheory>

    override fun retractAll(head: Struct): RetractResult<MutableTheory> = retractAll(Rule.of(head, Var.anonymous()))

    override fun abolish(indicator: Indicator): MutableTheory

    override fun replaceTags(tags: Map<String, Any>): MutableTheory

    /** A new, independent [MutableTheory] with the same clauses, [unificator] and tags. */
    override fun clone(): MutableTheory

    companion object {
        /** Creates an empty [MutableTheory] using the given [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator): MutableTheory = indexedOf(unificator, emptyList())

        /**
         * Creates a [MutableTheory] containing the given clauses, in order, using the given [unificator].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clause: Clause,
        ): MutableTheory = indexedOf(unificator, *clause)

        /**
         * Creates a [MutableTheory] containing the given clauses, in order, using the given [unificator].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableTheory = indexedOf(unificator, clauses)

        /**
         * Creates a [MutableTheory] containing the given clauses, in order, using the given [unificator].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): MutableTheory = indexedOf(unificator, clauses)

        /**
         * Creates a [MutableTheory] from clause factories, each run in a fresh [Scope], so that variables with the same
         * name in different clauses are different variables.
         */
        @JvmStatic
        @JsName("ofScopes")
        fun of(
            unificator: Unificator,
            vararg clauses: Scope.() -> Clause,
        ): MutableTheory = indexedOf(unificator, *clauses)

        /** Same as [empty]: every [MutableTheory] is indexed. */
        @JvmStatic
        @JsName("emptyIndexed")
        fun emptyIndexed(unificator: Unificator): MutableTheory = indexedOf(unificator, emptyList())

        /** Same as [of]: every [MutableTheory] is indexed. */
        @JvmStatic
        @JsName("indexedOf")
        fun indexedOf(
            unificator: Unificator,
            vararg clause: Clause,
        ): MutableTheory = indexedOf(unificator, clause.asIterable())

        /** Same as [of]: every [MutableTheory] is indexed. */
        @JvmStatic
        @JsName("indexedOfScopes")
        fun indexedOf(
            unificator: Unificator,
            vararg clauses: Scope.() -> Clause,
        ): MutableTheory =
            indexedOf(
                unificator,
                clauses.map {
                    Scope.empty(it)
                },
            )

        /** Same as [of]: every [MutableTheory] is indexed. */
        @JvmStatic
        @JsName("indexedOfSequence")
        fun indexedOf(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): MutableTheory = indexedOf(unificator, clauses.asIterable())

        /** Same as [of]: every [MutableTheory] is indexed. */
        @JvmStatic
        @JsName("indexedOfIterable")
        fun indexedOf(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableTheory = MutableIndexedTheory(unificator, clauses)
    }
}
