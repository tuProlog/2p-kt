package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.UnificationAware
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Taggable
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A Prolog knowledge base: an ordered collection of [Clause]s (facts, rules and directives) that can be
 * queried by unification against a goal, and grown or shrunk with the usual ISO-inspired vocabulary
 * (`assertA`/`assertZ`/`retract`/`abolish`).
 *
 * `Theory` is deliberately a thin, Prolog-flavoured façade: how clauses are actually stored, searched and
 * mutated is delegated to a `ClauseCollection` (see the `it.unibo.tuprolog.collections` package) chosen at
 * construction time. Two independent axes of variation exist, both hidden behind this same interface:
 * - **mutable vs. immutable**: a plain `Theory` is a persistent data structure — every `assertA`/`assertZ`/
 *   `retract` returns a *new* `Theory`, leaving `this` untouched; a [MutableTheory] (obtained via
 *   [toMutableTheory]) edits itself in place instead, which is cheaper when a program asserts/retracts
 *   clauses very frequently (e.g. a Prolog `dynamic` predicate).
 * - **indexed vs. listed**: an *indexed* theory (the default, see [indexedOf]) keeps clauses in a
 *   discrimination-tree-like structure (see `it.unibo.tuprolog.collections.rete.custom.ReteTree`) indexed by
 *   directive-vs-rule, functor, arity and first-argument shape, trading memory and update cost for fast
 *   lookup; a *listed* theory (see [listedOf]) is a thin wrapper around an ordered clause list, cheap to
 *   build and to keep in insertion order, but linear to query (each `get` scans and unifies against every
 *   clause). A knowledge base that is queried far more often than it is changed (a typical static, "library"
 *   theory) benefits from indexing; one that is asserted/retracted on nearly every resolution step, or kept
 *   small, may not be worth indexing at all — which is exactly why solvers (see `:solve`) build their static
 *   knowledge base with [of] (indexed) and their dynamic one with [listedOf].
 *
 * Example — building a small theory and querying it:
 * ```kotlin
 * val theory =
 *     Theory.indexedOf(
 *         Unificator.default,
 *         Fact.of(Struct.of("parent", Atom.of("alice"), Atom.of("bob"))),
 *         Fact.of(Struct.of("parent", Atom.of("bob"), Atom.of("carol"))),
 *     )
 * val goal = Struct.of("parent", Var.of("Who"), Atom.of("bob"))
 * theory[goal].toList() // rules/facts whose head *might* unify with `parent(Who, bob)`
 * ```
 *
 * @see MutableTheory
 * @see it.unibo.tuprolog.collections.ClauseCollection
 */
interface Theory :
    Iterable<Clause>,
    Taggable<Theory>,
    UnificationAware {
    /** Whether this [Theory] supports in-place mutation; `true` for [MutableTheory], `false` otherwise. */
    @JsName("isMutable")
    val isMutable: Boolean
        get() = false

    /** Returns a [Theory] with the same clauses as this one, but using the given [unificator] to match them. */
    @JsName("setUnificator")
    fun setUnificator(unificator: Unificator): Theory

    /** Returns a [MutableTheory] with the same clauses (and [Unificator]) as this one; `this`, if it already is one. */
    @JsName("toMutableTheory")
    fun toMutableTheory(): MutableTheory

    /** Returns an immutable [Theory] with the same clauses (and [Unificator]) as this one; `this`, if it already is one. */
    @JsName("toImmutableTheory")
    fun toImmutableTheory(): Theory

    /** All [Clause]s in this theory */
    @JsName("clauses")
    val clauses: Iterable<Clause>

    /** Only [clauses] that are [Rule]s */
    @JsName("rules")
    val rules: Iterable<Rule>
        get() =
            clauses
                .asSequence()
                .map { it.asRule() }
                .filterNotNull()
                .asIterable()

    /** Only [clauses] that are [Directive]s */
    @JsName("directives")
    val directives: Iterable<Directive>
        get() =
            clauses
                .asSequence()
                .map { it.asDirective() }
                .filterNotNull()
                .asIterable()

    /** The amount of clauses in this [Theory] */
    @JsName("size")
    val size: Long

    /** Whether this [Theory] is empty or not */
    @JsName("isEmpty")
    val isEmpty: Boolean

    /** Whether this [Theory] is full or not */
    @JsName("isNonEmpty")
    val isNonEmpty: Boolean

    /**
     * Adds all the clauses of the given [theory] after all the clauses of this one, returning the resulting
     * [Theory] (as a *new* instance, unless [theory] [is empty][Theory.isEmpty]).
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

    /** Checks if given clause is contained in this theory */
    @JsName("contains")
    operator fun contains(clause: Clause): Boolean

    /** Checks if given clause is present in this theory */
    @JsName("containsHead")
    operator fun contains(head: Struct): Boolean

    /**
     * Checks if clauses exist in this theory having the specified [indicator] as head.
     * @throws IllegalArgumentException if [indicator] is not [well-formed][Indicator.isWellFormed]
     */
    @JsName("containsIndicator")
    operator fun contains(indicator: Indicator): Boolean

    /** Retrieves the [Sequence] of clauses in this theory that could unify against the given [clause] */
    @JsName("get")
    operator fun get(clause: Clause): Sequence<Clause>

    /** Retrieves the [Sequence] of [Rule]s in this theory whose head could unify against the given [head] */
    @JsName("getByHead")
    operator fun get(head: Struct): Sequence<Rule>

    /**
     * Retrieves all the [Rule]s in this theory having the specified [indicator] as head.
     * @throws IllegalArgumentException if [indicator] is not [well-formed][Indicator.isWellFormed]
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
    fun assertA(struct: Struct): Theory = assertA(Fact.of(struct))

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
    fun assertZ(struct: Struct): Theory = assertZ(Fact.of(struct))

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
     * Tries to delete, from this theory, one clause unifying against each of the given [clauses] patterns.
     * @see retract
     */
    @JsName("retractIterable")
    fun retract(clauses: Iterable<Clause>): RetractResult<Theory>

    /**
     * Tries to delete, from this theory, one clause unifying against each of the given [clauses] patterns.
     * @see retract
     */
    @JsName("retractSequence")
    fun retract(clauses: Sequence<Clause>): RetractResult<Theory>

    /** Tries to delete a clause whose head unifies against the given [head]; equivalent to `retract(Rule.of(head, _))` */
    @JsName("retractByHead")
    fun retract(head: Struct): RetractResult<Theory> = retract(Rule.of(head, Var.anonymous()))

    /** Tries to delete all the clauses in this theory unifying against the given [clause] */
    @JsName("retractAll")
    fun retractAll(clause: Clause): RetractResult<Theory>

    /** Tries to delete all the clauses whose head unifies against the given [head]; equivalent to `retractAll(Rule.of(head, _))` */
    @JsName("retractAllByHead")
    fun retractAll(head: Struct): RetractResult<Theory> = retractAll(Rule.of(head, Var.anonymous()))

    /**
     * Removes from this theory all the clauses whose head has the given [indicator] as functor/arity, mirroring
     * ISO Prolog's `abolish/1`.
     * @throws IllegalArgumentException if [indicator] is not [well-formed][Indicator.isWellFormed]
     */
    @JsName("abolish")
    fun abolish(indicator: Indicator): Theory

    /** An enhanced toString that prints the theory in a Prolog program format, if [asPrologText] is `true` */
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

    /** Returns a copy of this [Theory]; for an immutable theory this may return `this` unchanged. */
    @JsName("clone")
    fun clone(): Theory

    companion object {
        /** Creates an empty [Theory] */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator): Theory = indexedOf(unificator, emptySequence())

        /** Creates an empty [Theory], using the [default unificator][Unificator.default] */
        @JvmStatic
        @JsName("emptyWithDefaultUnificator")
        fun empty(): Theory = empty(Unificator.default)

        /** Creates a [Theory], containing the given clauses */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clause: Clause,
        ): Theory = indexedOf(unificator, *clause)

        /** Creates a [Theory], containing the given clauses, using the [default unificator][Unificator.default] */
        @JvmStatic
        @JsName("ofWithDefaultUnificator")
        fun of(vararg clause: Clause): Theory = indexedOf(Unificator.default, *clause)

        /** Creates a [Theory], containing the given clauses */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): Theory = indexedOf(unificator, clauses)

        /** Creates a [Theory], containing the given clauses, using the [default unificator][Unificator.default] */
        @JvmStatic
        @JsName("ofIterableWithDefaultUnificator")
        fun of(clauses: Iterable<Clause>): Theory = indexedOf(Unificator.default, clauses)

        /** Creates a [Theory], containing the given clauses */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): Theory = indexedOf(unificator, clauses)

        /** Creates a [Theory], containing the given clauses, using the [default unificator][Unificator.default] */
        @JvmStatic
        @JsName("ofSequenceWithDefaultUnificator")
        fun of(clauses: Sequence<Clause>): Theory = indexedOf(Unificator.default, clauses)

        /** Let developers easily create a [Theory], while avoiding variables names clashing by using a
         * different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("ofScopes")
        fun of(
            unificator: Unificator,
            vararg clauses: Scope.() -> Clause,
        ): Theory = indexedOf(unificator, *clauses)

        /** Let developers easily create a [Theory] using the [default unificator][Unificator.default], while
         * avoiding variables names clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("ofScopesWithDefaultUnificator")
        fun of(vararg clauses: Scope.() -> Clause): Theory = indexedOf(Unificator.default, *clauses)

        /** Creates an empty [Theory] backed by an indexed data structure */
        @JvmStatic
        @JsName("emptyIndexed")
        fun emptyIndexed(unificator: Unificator): Theory = indexedOf(unificator, emptyList())

        /** Creates a [Theory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOf")
        fun indexedOf(
            unificator: Unificator,
            vararg clause: Clause,
        ): Theory = indexedOf(unificator, clause.asIterable())

        /** Let developers easily create a [Theory] backed by an indexed data structure, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
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

        /** Creates a [Theory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfSequence")
        fun indexedOf(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): Theory = indexedOf(unificator, clauses.asIterable())

        /** Creates a [Theory] backed by an indexed data structure, containing the given clauses */
        @JvmStatic
        @JsName("indexedOfIterable")
        fun indexedOf(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): Theory = IndexedTheoryFactory.Default.theoryOf(clauses, unificator)

        /** Creates an empty [Theory] backed by a list */
        @JvmStatic
        @JsName("emptyListed")
        fun emptyListed(unificator: Unificator): Theory = listedOf(unificator, emptySequence())

        /** Creates a [Theory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOf")
        fun listedOf(
            unificator: Unificator,
            vararg clause: Clause,
        ): Theory = listedOf(unificator, clause.asIterable())

        /** Let developers easily create a [Theory] backed by a list, while avoiding variables names
         * clashing by using a different [Scope] for each [Clause] */
        @JvmStatic
        @JsName("listedOfScopes")
        fun listedOf(
            unificator: Unificator,
            vararg clause: Scope.() -> Clause,
        ): Theory =
            listedOf(
                unificator,
                clause.map {
                    Scope.empty(it)
                },
            )

        /** Creates a [Theory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfSequence")
        fun listedOf(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): Theory = listedOf(unificator, clauses.asIterable())

        /** Creates a [Theory] backed by a list, containing the given clauses */
        @JvmStatic
        @JsName("listedOfIterable")
        fun listedOf(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): Theory = ListedTheoryFactory.Default.theoryOf(clauses, unificator)
    }
}
