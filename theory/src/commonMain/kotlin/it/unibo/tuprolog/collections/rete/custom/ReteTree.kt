package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.nodes.RootNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**
 * A RETE-style discrimination network storing [Clause]s and indexing them for fast retrieval by unification.
 *
 * Clauses are first split into [directives] and [rules] (see [Directive]/[Rule]), then, within each of those
 * two families, indexed by their head's functor, then by arity, then recursively by the shape of their first
 * argument (variable, atomic, numeric or compound — see the `it.unibo.tuprolog.collections.rete.custom.leaf`
 * package), narrowing the set of clauses that [get] has to actually attempt to unify against a query. This is
 * the storage engine backing both `it.unibo.tuprolog.collections.ClauseQueue` (ordered) and
 * `it.unibo.tuprolog.collections.ClauseMultiSet` (unordered), and therefore, transitively, `Theory` itself.
 * @see it.unibo.tuprolog.theory.Theory
 */
interface ReteTree {
    /** The [Unificator] used to match [Clause]s stored in this tree against query clauses. */
    @JsName("unificator")
    val unificator: Unificator

    /** Whether this [ReteTree] preserves insertion order among clauses of the same family (functor/arity/shape);
     *  an unordered tree may return matching clauses in any order, but does not support [assertA]. */
    val isOrdered: Boolean

    /**Returns all the [Clause] this [ReteTree] is storing*/
    val clauses: Sequence<Clause>

    /** Returns all the [Rule]s this [ReteTree] is storing. */
    val rules: Sequence<Rule>

    /** Returns all the [Directive]s this [ReteTree] is storing. */
    val directives: Sequence<Directive>

    /**Returns the number of [Clause] stored in this tree*/
    val size: Int

    /** Whether this [ReteTree] stores no clause at all. */
    val isEmpty: Boolean

    /**Reads all the clauses matching the given [Clause]*/
    fun get(clause: Clause): Sequence<Clause>

    /**Tells if the given [Clause] is stored in this [ReteTree]*/
    operator fun contains(clause: Clause): Boolean = get(clause).any()

    /**
     * Inserts the given [clause] before all other clauses of its own family (same directive-vs-rule status,
     * functor and arity) currently stored in this tree, mutating it in place.
     * @throws UnsupportedOperationException if this [ReteTree] [is not ordered][isOrdered]
     */
    fun assertA(clause: Clause)

    /** Inserts the given [clause] after all other clauses of its own family currently stored in this tree,
     *  mutating it in place. */
    fun assertZ(clause: Clause)

    /**Retract the first occurrence of the given [Clause] from this [ReteTree]. The meaning of "first"
     * may vary between implementations*/
    fun retractFirst(clause: Clause): Sequence<Clause>

    /**Retracts only the given number of matching clauses from this [ReteTree]*/
    fun retractOnly(
        clause: Clause,
        limit: Int,
    ): Sequence<Clause>

    /**Retracts all the matching clauses from this [ReteTree]*/
    fun retractAll(clause: Clause): Sequence<Clause>

    /**Produces a fully instantiated complete copy of this [ReteTree]*/
    fun deepCopy(): ReteTree

    companion object {
        /**Creates an empty unordered [ReteTree]*/
        fun emptyUnordered(unificator: Unificator): ReteTree = unordered(unificator, emptyList())

        /**Creates an unordered ReteTree based on the given [Iterable]*/
        fun unordered(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ReteTree = RootNode(unificator, clauses, false)

        /**Creates an unordered ReteTree based on the given vararg*/
        fun unordered(
            unificator: Unificator,
            vararg clauses: Clause,
        ): ReteTree = unordered(unificator, listOf(*clauses))

        /**Creates an empty ordered [ReteTree]*/
        fun emptyOrdered(unificator: Unificator): ReteTree = ordered(unificator, emptyList())

        /**Creates an ordered ReteTree based on the given [Iterable]*/
        fun ordered(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ReteTree = RootNode(unificator, clauses, true)

        /**Creates an ordered ReteTree based on the given vararg*/
        fun ordered(
            unificator: Unificator,
            vararg clauses: Clause,
        ): ReteTree = ordered(unificator, listOf(*clauses))
    }
}
