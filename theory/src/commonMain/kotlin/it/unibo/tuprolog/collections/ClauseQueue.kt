package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.ReteClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.itemWiseEquals
import it.unibo.tuprolog.utils.itemWiseHashCode
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface ClauseQueue : ClauseCollection {

    /** Gives a freshly produced [ClauseQueue] including the given [Clause] in the first position and the content
     *  of this one **/
    @JsName("addFirst")
    fun addFirst(clause: Clause): ClauseQueue

    /** Gives a freshly produced [ClauseQueue] including the given [Clause] in the last position and the content
     *  of this one **/
    @JsName("addLast")
    fun addLast(clause: Clause): ClauseQueue

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause], scanning from data structure from
     *  the first position to the last one **/
    @JsName("getFifoOrdered")
    fun getFifoOrdered(clause: Clause): Sequence<Clause>

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause], scanning from data structure from
     *  the last position to the first **/
    @JsName("getLifoOrdered")
    fun getLifoOrdered(clause: Clause): Sequence<Clause>

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause]. Analogous to [getFifoOrdered] **/
    operator fun get(clause: Clause): Sequence<Clause> =
        getFifoOrdered(clause)

    /** Gives a freshly produced [ClauseQueue] including the given [Clause] and the content of this one.
     *  Analogous to [addFirst] **/
    override fun add(clause: Clause): ClauseQueue =
        addLast(clause)

    /** Gives a freshly produced [ClauseQueue] including all the given [Clause] and the content of this one **/
    override fun addAll(clauses: Iterable<Clause>): ClauseQueue

    /** Retrieve the first [Clause] unifying the given one, searching from the first position **/
    @JsName("retrieveFirst")
    fun retrieveFirst(clause: Clause): RetrieveResult<out ClauseQueue>

    /** Retrieve the first [Clause] unifying the given one. Analogous to [retrieveFirst] **/
    override fun retrieve(clause: Clause): RetrieveResult<out ClauseQueue> =
        retrieveFirst(clause)

    /** Retrieve all the [Clause] unifying the given one **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseQueue>

    companion object {

        /** Creates an empty [ClauseQueue] **/
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator): ClauseQueue =
            of(unificator, emptyList())

        /** Creates a [ClauseQueue] with given clauses */
        @JvmStatic
        @JsName("of")
        fun of(unificator: Unificator, vararg clause: Clause): ClauseQueue =
            of(unificator, clause.asIterable())

        /** Let developers easily create a [ClauseQueue] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("ofScopes")
        fun of(unificator: Unificator, vararg clause: Scope.() -> Clause): ClauseQueue =
            of(
                unificator,
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [ClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("ofSequence")
        fun of(unificator: Unificator, clauses: Sequence<Clause>): ClauseQueue =
            of(unificator, clauses.asIterable())

        /** Creates a [ClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("ofIterable")
        fun of(unificator: Unificator, clauses: Iterable<Clause>): ClauseQueue =
            ReteClauseQueue(unificator, clauses)

        @JvmStatic
        @JsName("areEquals")
        fun equals(queue1: ClauseQueue, queue2: ClauseQueue): Boolean {
            return itemWiseEquals(queue1, queue2)
        }

        @JvmStatic
        @JsName("computeHashCode")
        fun hashCode(queue: ClauseQueue): Int {
            return itemWiseHashCode(
                ClauseQueue::class,
                itemWiseHashCode(queue)
            )
        }
    }
}
