package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A general-purpose, immutable container of [Clause]s, matched by unification rather than by equality: this is
 * the low-level storage abstraction that a `it.unibo.tuprolog.theory.Theory` builds its Prolog-flavoured API
 * (`assertA`/`assertZ`/`retract`) on top of. Every mutating operation ([add], [addAll], [retrieve],
 * [retrieveAll]) returns a *new* collection, leaving `this` untouched — see [MutableClauseCollection] for the
 * in-place counterpart.
 *
 * The default implementations returned by this interface's factory methods (and by [ClauseQueue]/
 * [ClauseMultiSet]) are backed by a RETE-style discrimination tree (see
 * `it.unibo.tuprolog.collections.rete.custom.ReteTree`) indexed by directive-vs-rule, functor, arity and
 * first-argument shape, so [get]/[retrieve]-like lookups only re-check clauses that could plausibly unify with
 * the query, rather than scanning the whole collection.
 *
 * @see ClauseQueue
 * @see ClauseMultiSet
 */
interface ClauseCollection : Iterable<Clause> {
    /** The [Unificator] used to match clauses against each other in this collection. */
    @JsName("unificator")
    val unificator: Unificator

    /** Only the clauses in this collection that are [Directive]s. */
    @JsName("directive")
    val directives: Iterable<Directive>

    /** Only the clauses in this collection that are [Rule]s. */
    @JsName("rules")
    val rules: Iterable<Rule>

    /** Computes the size of the [ClauseCollection] **/
    @JsName("size")
    val size: Int

    /** Tells if the [ClauseCollection] contains any [Clause] **/
    @JsName("isEmpty")
    fun isEmpty(): Boolean

    /** Tells if the [ClauseCollection] contains at least one [Clause]; the negation of [isEmpty]. **/
    @JsName("isNonEmpty")
    fun isNonEmpty(): Boolean

    /** Tells if the [ClauseCollection] contains a clause unifying against the given [element] **/
    @JsName("contains")
    operator fun contains(element: Clause): Boolean

    /** Tells if, for each of the given [elements], the [ClauseCollection] contains a unifying clause **/
    @JsName("containsAll")
    fun containsAll(elements: Iterable<Clause>): Boolean

    /** Gives a freshly produced [ClauseCollection] including the given [Clause] and the content of this one **/
    @JsName("add")
    fun add(clause: Clause): ClauseCollection

    /** Gives a freshly produced [ClauseCollection] including all the given [Clause] and the content of this one **/
    @JsName("addAll")
    fun addAll(clauses: Iterable<Clause>): ClauseCollection

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting the given [Clause]
     *  from this [ClauseCollection] **/
    @JsName("retrieve")
    fun retrieve(clause: Clause): RetrieveResult<out ClauseCollection>

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting all the given [Clause]
     *  from this [ClauseCollection] **/
    @JsName("retrieveAll")
    fun retrieveAll(clause: Clause): RetrieveResult<out ClauseCollection>

    /** Iterates over all the clauses in this collection, in an order that depends on the concrete
     *  implementation (e.g. insertion order for a [ClauseQueue], unspecified for a [ClauseMultiSet]). */
    override fun iterator(): Iterator<Clause>

    companion object {
        /** Creates an empty [ClauseMultiSet] **/
        @JvmStatic
        @JsName("emptyMultiSet")
        fun emptyMultiSet(unificator: Unificator): ClauseMultiSet = multiSetOf(unificator, emptyList())

        /** Creates a [ClauseMultiSet] with given clauses */
        @JvmStatic
        @JsName("multiSetOf")
        fun multiSetOf(
            unificator: Unificator,
            vararg clause: Clause,
        ): ClauseMultiSet = multiSetOf(unificator, clause.asIterable())

        /** Let developers easily create a [ClauseMultiSet] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("multiSetOfScopes")
        fun multiSetOf(
            unificator: Unificator,
            vararg clause: Scope.() -> Clause,
        ): ClauseMultiSet =
            multiSetOf(
                unificator,
                clause.map {
                    Scope.empty(it)
                },
            )

        /** Creates a [ClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfSequence")
        fun multiSetOf(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): ClauseMultiSet = multiSetOf(unificator, clauses.asIterable())

        /** Creates a [ClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfIterable")
        fun multiSetOf(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ClauseMultiSet = ClauseMultiSet.of(unificator, clauses)

        /** Creates an empty [ClauseQueue] **/
        @JvmStatic
        @JsName("emptyQueue")
        fun emptyQueue(unificator: Unificator): ClauseQueue = queueOf(unificator, emptyList())

        /** Creates a [ClauseQueue] with given clauses */
        @JvmStatic
        @JsName("queueOf")
        fun queueOf(
            unificator: Unificator,
            vararg clause: Clause,
        ): ClauseQueue = queueOf(unificator, clause.asIterable())

        /** Let developers easily create a [ClauseQueue] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("queueOfScopes")
        fun queueOf(
            unificator: Unificator,
            vararg clause: Scope.() -> Clause,
        ): ClauseQueue =
            queueOf(
                unificator,
                clause.map {
                    Scope.empty(it)
                },
            )

        /** Creates a [ClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("queueOfSequence")
        fun queueOf(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): ClauseQueue = queueOf(unificator, clauses.asIterable())

        /** Creates a [ClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("queueOfIterable")
        fun queueOf(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ClauseQueue = ClauseQueue.of(unificator, clauses)
    }
}
