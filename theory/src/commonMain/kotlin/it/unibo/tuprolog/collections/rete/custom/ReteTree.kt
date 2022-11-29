package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.nodes.RootNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

interface ReteTree {

    @JsName("unificator")
    val unificator: Unificator

    /**Checks if the values this [ReteTree] produces are to be considered as order-sensitive*/
    val isOrdered: Boolean

    /**Returns all the [Clause] this [ReteTree] is storing*/
    val clauses: Sequence<Clause>

    val rules: Sequence<Rule>

    val directives: Sequence<Directive>

    /**Returns the number of [Clause] stored in this tree*/
    val size: Int

    val isEmpty: Boolean

    /**Reads all the clauses matching the given [Clause]*/
    fun get(clause: Clause): Sequence<Clause>

    /**Tells if the given [Clause] is stored in this [ReteTree]*/
    operator fun contains(clause: Clause): Boolean {
        return get(clause).any()
    }

    /**Tries to insert the given [Clause] as the first occurrence of its own family*/
    fun assertA(clause: Clause)

    /**Insert the given [Clause] as the first occurrence of its own family*/
    fun assertZ(clause: Clause)

    /**Retract the first occurrence of the given [Clause] from this [ReteTree]. The meaning of "first"
     * may vary between implementations*/
    fun retractFirst(clause: Clause): Sequence<Clause>

    /**Retracts only the given number of matching clauses from this [ReteTree]*/
    fun retractOnly(clause: Clause, limit: Int): Sequence<Clause>

    /**Retracts all the matching clauses from this [ReteTree]*/
    fun retractAll(clause: Clause): Sequence<Clause>

    /**Produces a fully instantiated complete copy of this [ReteTree]*/
    fun deepCopy(): ReteTree

    companion object {
        /**Creates an empty unordered [ReteTree]*/
        fun emptyUnordered(unificator: Unificator): ReteTree =
            unordered(unificator, emptyList())

        /**Creates an unordered ReteTree based on the given [Iterable]*/
        fun unordered(unificator: Unificator, clauses: Iterable<Clause>): ReteTree =
            RootNode(unificator, clauses, false)

        /**Creates an unordered ReteTree based on the given vararg*/
        fun unordered(unificator: Unificator, vararg clauses: Clause): ReteTree =
            unordered(unificator, listOf(*clauses))

        /**Creates an empty ordered [ReteTree]*/
        fun emptyOrdered(unificator: Unificator,): ReteTree =
            ordered(unificator, emptyList())

        /**Creates an ordered ReteTree based on the given [Iterable]*/
        fun ordered(unificator: Unificator, clauses: Iterable<Clause>): ReteTree =
            RootNode(unificator, clauses, true)

        /**Creates an ordered ReteTree based on the given vararg*/
        fun ordered(unificator: Unificator, vararg clauses: Clause): ReteTree =
            ordered(unificator, listOf(*clauses))
    }
}
