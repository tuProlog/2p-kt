package it.unibo.tuprolog.theory

import it.unibo.tuprolog.collections.ClauseDeque
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Taggable
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A Prolog knowledge base: an ordered collection of [Clause]s (facts, rules and directives) that can be
 * queried by unification against a goal, and grown or shrunk with the usual ISO-inspired vocabulary
 * (`assertA`/`assertZ`/`retract`/`abolish`).
 *
 * A `Theory` is a [ClauseDeque] (see the `it.unibo.tuprolog.collections` package), taggable, and comes in two
 * flavours:
 * - a plain `Theory` is **immutable** and optimised for **reads** (static knowledge bases). Every
 *   `assertA`/`assertZ`/`retract` returns a new `Theory` sharing with `this` every procedure it does not touch, or
 *   `this` itself when nothing changes (e.g. a failed `retract`);
 * - a [MutableTheory] (obtained via [toMutableTheory]) is optimised for **writes** (dynamic knowledge bases): it
 *   edits itself in place, in amortised constant time after lookup, and its lookups follow the logical update view.
 *
 * Both flavours group clauses by procedure (functor and arity of the head) and, within a procedure of at least 4
 * clauses, build argument indexes *on demand*: the first lookup binding an argument builds an index on it, and every
 * lookup then uses, among the arguments it binds, the index yielding the fewest candidates (demand-driven, or
 * just-in-time, multi-argument indexing, as in YAP and SWI-Prolog). Where an argument's index cannot tell clauses
 * apart, lookups also index the arguments of compound terms, down to depth 4 (SWI-Prolog's *deep indexing*).
 * Index keys come from the [Unificator] (see [Unificator.indexKey]), so indexing is consistent with the unificator's
 * notion of equality.
 *
 * Example — building a small theory and querying it:
 * ```kotlin
 * val theory =
 *     Theory.of(
 *         Fact.of(Struct.of("parent", Atom.of("alice"), Atom.of("bob"))),
 *         Fact.of(Struct.of("parent", Atom.of("bob"), Atom.of("carol"))),
 *     )
 * val goal = Struct.of("parent", Var.of("Who"), Atom.of("bob"))
 * theory[goal].toList() // [parent(alice, bob)]: the rules whose head unifies with `parent(Who, bob)`
 * ```
 *
 * References:
 * - V. Santos Costa, K. Sagonas, R. Lopes. *Demand-Driven Indexing of Prolog Clauses*. ICLP 2007, LNCS 4670.
 * - J. Wielemaker, T. Schrijvers, M. Triska, T. Lager. *SWI-Prolog*. Theory and Practice of Logic Programming
 *   12(1-2), 2012; and the SWI-Prolog manual, section "Just-in-time clause indexing".
 * - D. H. D. Warren. *An Abstract Prolog Instruction Set*. SRI Technical Note 309, 1983 (first-argument indexing,
 *   which this scheme generalises to any argument).
 *
 * @see MutableTheory
 * @see it.unibo.tuprolog.collections.ClauseCollection
 */
interface Theory :
    ClauseDeque,
    Taggable<Theory> {
    /** Whether this [Theory] supports in-place mutation; `true` for [MutableTheory], `false` otherwise. */
    @JsName("isMutable")
    val isMutable: Boolean
        get() = false

    /**
     * Returns a [Theory] with the same clauses and tags as this one, but using the given [unificator] to match (and
     * index) them: `this`, if [unificator] equals the current one, a new theory otherwise (also for a [MutableTheory]).
     */
    @JsName("setUnificator")
    fun setUnificator(unificator: Unificator): Theory

    /**
     * Returns a [MutableTheory] with the same clauses, [Unificator] and tags as this one: `this`, if it already is one,
     * a copy otherwise.
     */
    @JsName("toMutableTheory")
    fun toMutableTheory(): MutableTheory

    /**
     * Returns an immutable [Theory] with the same clauses, [Unificator] and tags as this one: `this`, if it already is
     * one, a copy otherwise (so later changes to a [MutableTheory] do not affect the result).
     */
    @JsName("toImmutableTheory")
    fun toImmutableTheory(): Theory

    /** A new [MutableTheory] with the same clauses, [Unificator] and tags: unlike [toMutableTheory], always a copy. */
    override fun toMutable(): MutableTheory = toMutableTheory().let { if (it === this) it.clone() else it }

    /** Same as [toImmutableTheory]. */
    override fun toImmutable(): Theory = toImmutableTheory()

    /** All the [Clause]s in this theory, in order. */
    override val clauses: Iterable<Clause>

    /** The number of clauses in this [Theory]. */
    override val size: Long

    /** Whether this [Theory] contains no clause. */
    override val isEmpty: Boolean

    /** Whether this [Theory] contains at least one clause. */
    override val isNonEmpty: Boolean

    /**
     * Adds all the clauses of the given [theory] after all the clauses of this one, keeping this theory's
     * [Unificator] and tags; equivalent to `assertZ(theory.clauses)`. Returns `this` if [theory] is empty.
     * @throws IllegalArgumentException if any clause of [theory] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("plusTheory")
    operator fun plus(theory: Theory): Theory

    /**
     * Adds the given [clause] to this [Theory]; equivalent to [assertZ].
     * @throws IllegalArgumentException if [clause] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("plus")
    operator fun plus(clause: Clause): Theory = assertZ(clause)

    /**
     * Whether some clause of this theory *unifies* with [clause], according to the [unificator]. This is not
     * membership: use [containsVariant] to check whether a clause is stored, up to variable renaming.
     */
    override operator fun contains(clause: Clause): Boolean

    /** Whether some rule of this theory has a head unifying with [head]; equivalent to `contains(Rule.of(head, _))`. */
    @JsName("containsHead")
    operator fun contains(head: Compound): Boolean

    /**
     * Checks if clauses exist in this theory having the specified [indicator] as head.
     * @throws IllegalArgumentException if [indicator] is not [concrete][Indicator.isConcrete]
     */
    @JsName("containsIndicator")
    operator fun contains(indicator: Indicator): Boolean

    /** The clauses of this theory unifying with [clause], lazily, in order. */
    override operator fun get(clause: Clause): Sequence<Clause>

    /** The [Rule]s of this theory whose head unifies with [head] (whatever their body), lazily, in order. */
    @JsName("getByHead")
    operator fun get(head: Compound): Sequence<Rule>

    /**
     * Retrieves all the [Rule]s in this theory having the specified [indicator] as head.
     * @throws IllegalArgumentException if [indicator] is not [concrete][Indicator.isConcrete]
     */
    @JsName("getByIndicator")
    operator fun get(indicator: Indicator): Sequence<Rule>

    /**
     * Returns a [Theory] with the given [clause] inserted before all other clauses of this one.
     * @throws IllegalArgumentException if [clause] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("assertA")
    fun assertA(clause: Clause): Theory

    /** Returns a [Theory] with the [Fact] built from [struct] inserted before all other clauses of this one */
    @JsName("assertAFact")
    fun assertA(struct: Compound): Theory = assertA(Fact.of(struct))

    /**
     * Returns a [Theory] with the given [clauses] inserted, in order, before all other clauses of this one.
     * @throws IllegalArgumentException if any of [clauses] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("assertAIterable")
    fun assertA(clauses: Iterable<Clause>): Theory

    /**
     * Returns a [Theory] with the given [clauses] inserted, in order, before all other clauses of this one.
     * @throws IllegalArgumentException if any of [clauses] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("assertASequence")
    fun assertA(clauses: Sequence<Clause>): Theory

    /**
     * Returns a [Theory] with the given [clause] inserted after all other clauses of this one.
     * @throws IllegalArgumentException if [clause] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("assertZ")
    fun assertZ(clause: Clause): Theory

    /** Returns a [Theory] with the [Fact] built from [struct] inserted after all other clauses of this one */
    @JsName("assertZFact")
    fun assertZ(struct: Compound): Theory = assertZ(Fact.of(struct))

    /**
     * Returns a [Theory] with the given [clauses] inserted, in order, after all other clauses of this one.
     * @throws IllegalArgumentException if any of [clauses] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("assertZIterable")
    fun assertZ(clauses: Iterable<Clause>): Theory

    /**
     * Returns a [Theory] with the given [clauses] inserted, in order, after all other clauses of this one.
     * @throws IllegalArgumentException if any of [clauses] is not [well-formed][Clause.isWellFormed]
     */
    @JsName("assertZSequence")
    fun assertZ(clauses: Sequence<Clause>): Theory

    /**
     * Tries to delete the first clause in this theory unifying against the given [clause], returning a
     * [RetractResult.Success] wrapping the resulting [Theory] and the removed clause, or a [RetractResult.Failure]
     * wrapping this same theory if no clause matched.
     */
    @JsName("retract")
    fun retract(clause: Clause): RetractResult<Theory>

    /**
     * Tries to delete, from this theory, the first clause unifying with each of the given [clauses] patterns, in
     * order. Succeeds if at least one pattern matched, with [RetractResult.clauses] listing the removed clauses;
     * fails if none did.
     * @see retract
     */
    @JsName("retractIterable")
    fun retract(clauses: Iterable<Clause>): RetractResult<Theory>

    /**
     * Tries to delete, from this theory, the first clause unifying with each of the given [clauses] patterns, in
     * order. Succeeds if at least one pattern matched, with [RetractResult.clauses] listing the removed clauses;
     * fails if none did.
     * @see retract
     */
    @JsName("retractSequence")
    fun retract(clauses: Sequence<Clause>): RetractResult<Theory>

    /** Tries to delete a clause whose head unifies against the given [head]; equivalent to `retract(Rule.of(head, _))` */
    @JsName("retractByHead")
    fun retract(head: Compound): RetractResult<Theory> = retract(Rule.of(head, Var.anonymous()))

    /**
     * Tries to delete all the clauses in this theory unifying with the given [clause], returning a
     * [RetractResult.Success] wrapping the resulting [Theory] and the removed clauses (in order), or a
     * [RetractResult.Failure] wrapping this same theory if no clause matched.
     */
    @JsName("retractAll")
    fun retractAll(clause: Clause): RetractResult<Theory>

    /** Tries to delete all the clauses whose head unifies against the given [head]; equivalent to `retractAll(Rule.of(head, _))` */
    @JsName("retractAllByHead")
    fun retractAll(head: Compound): RetractResult<Theory> = retractAll(Rule.of(head, Var.anonymous()))

    /**
     * Removes from this theory all the clauses whose head has the given [indicator] as functor/arity, mirroring
     * ISO Prolog's `abolish/1`.
     * @throws IllegalArgumentException if [indicator] is not [concrete][Indicator.isConcrete]
     */
    @JsName("abolish")
    fun abolish(indicator: Indicator): Theory

    /**
     * If [asPrologText] is `true`, the clauses of this theory as Prolog source text, one per line, each terminated by
     * a full stop (`"f(a).\nf(b).\n"`; empty for an empty theory); otherwise, the same as [toString].
     */
    @JsName("toStringAsProlog")
    fun toString(asPrologText: Boolean): String

    /**
     * Checks whether this theory and [other] contain the same clauses, in the same order. If [useVarCompleteName]
     * is `true`, variables are compared also by their (possibly generated) complete name, rather than only by
     * their position within each clause.
     */
    @JsName("equalsUsingVarCompleteNames")
    fun equals(
        other: Theory,
        useVarCompleteName: Boolean,
    ): Boolean

    /**
     * Theories are equal when they are both [Theory]s (either flavour) holding equal clauses, in the same order:
     * the same as `equals(other, useVarCompleteName = true)`. Unifiers and tags are ignored.
     */
    override fun equals(other: Any?): Boolean

    /** Consistent with [equals]. */
    override fun hashCode(): Int

    /** Returns a copy of this [Theory], with the same tags: `this`, for an immutable theory. */
    @JsName("clone")
    fun clone(): Theory

    companion object {
        /** Creates an empty [Theory] using the given [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator): Theory = indexedOf(unificator, emptySequence())

        /** Creates an empty [Theory], using the [default unificator][Unificator.default] */
        @JvmStatic
        @JsName("emptyWithDefaultUnificator")
        fun empty(): Theory = empty(Unificator.default)

        /**
         * Creates a [Theory] containing the given clauses, in order, using the given [unificator].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clause: Clause,
        ): Theory = indexedOf(unificator, *clause)

        /**
         * Creates a [Theory] containing the given clauses, in order, using the [default unificator][Unificator.default].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("ofWithDefaultUnificator")
        fun of(vararg clause: Clause): Theory = indexedOf(Unificator.default, *clause)

        /**
         * Creates a [Theory] containing the given clauses, in order, using the given [unificator].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): Theory = indexedOf(unificator, clauses)

        /**
         * Creates a [Theory] containing the given clauses, in order, using the [default unificator][Unificator.default].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("ofIterableWithDefaultUnificator")
        fun of(clauses: Iterable<Clause>): Theory = indexedOf(Unificator.default, clauses)

        /**
         * Creates a [Theory] containing the given clauses, in order, using the given [unificator].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): Theory = indexedOf(unificator, clauses)

        /**
         * Creates a [Theory] containing the given clauses, in order, using the [default unificator][Unificator.default].
         * @throws IllegalArgumentException if any clause is not [well-formed][Clause.isWellFormed]
         */
        @JvmStatic
        @JsName("ofSequenceWithDefaultUnificator")
        fun of(clauses: Sequence<Clause>): Theory = indexedOf(Unificator.default, clauses)

        /**
         * Creates a [Theory] from clause factories, each run in a fresh [Scope], so that variables with the same name
         * in different clauses are different variables.
         */
        @JvmStatic
        @JsName("ofScopes")
        fun of(
            unificator: Unificator,
            vararg clauses: Scope.() -> Clause,
        ): Theory = indexedOf(unificator, *clauses)

        /**
         * Creates a [Theory] using the [default unificator][Unificator.default] from clause factories, each run in a
         * fresh [Scope], so that variables with the same name in different clauses are different variables.
         */
        @JvmStatic
        @JsName("ofScopesWithDefaultUnificator")
        fun of(vararg clauses: Scope.() -> Clause): Theory = indexedOf(Unificator.default, *clauses)

        /** Same as [empty]: every [Theory] is indexed. */
        @JvmStatic
        @JsName("emptyIndexed")
        fun emptyIndexed(unificator: Unificator): Theory = indexedOf(unificator, emptyList())

        /** Same as [of]: every [Theory] is indexed. */
        @JvmStatic
        @JsName("indexedOf")
        fun indexedOf(
            unificator: Unificator,
            vararg clause: Clause,
        ): Theory = indexedOf(unificator, clause.asIterable())

        /** Same as [of]: every [Theory] is indexed. */
        @JvmStatic
        @JsName("indexedOfScopes")
        fun indexedOf(
            unificator: Unificator,
            vararg clauses: Scope.() -> Clause,
        ): Theory =
            indexedOf(
                unificator,
                clauses.map {
                    Scope.empty(it)
                },
            )

        /** Same as [of]: every [Theory] is indexed. */
        @JvmStatic
        @JsName("indexedOfSequence")
        fun indexedOf(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): Theory = indexedOf(unificator, clauses.asIterable())

        /** Same as [of]: every [Theory] is indexed. */
        @JvmStatic
        @JsName("indexedOfIterable")
        fun indexedOf(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): Theory = IndexedTheoryFactory.Default.theoryOf(clauses, unificator)
    }
}
