package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableReteClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.itemWiseHashCode
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface MutableClauseQueue : ClauseQueue {

    /** Adds the given [Clause] as the first element in this [MutableClauseQueue] **/
    override fun addFirst(clause: Clause): MutableClauseQueue

    /** Adds the given [Clause] as the last element in this [MutableClauseQueue] **/
    override fun addLast(clause: Clause): MutableClauseQueue

    /** Adds the given [Clause] to this [MutableClauseQueue]. Analogous to [addLast]**/
    override fun add(clause: Clause): MutableClauseQueue

    /** Adds all the given [Clause] to this [MutableClauseQueue] **/
    override fun addAll(clauses: Iterable<Clause>): MutableClauseQueue

    /** Retrieves the first [Clause] unifying the given one, searching from the first position **/
    override fun retrieveFirst(clause: Clause): RetrieveResult<out MutableClauseQueue>

    /** Retrieves the first occurrence of the given [Clause] from this [MutableClauseQueue] as a [RetrieveResult] **/
    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseQueue>

    /** Retrieves all the occurrences of the given [Clause] from this [MutableClauseQueue] as a [RetrieveResult] **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseQueue>

    companion object {

        /** Creates an empty [MutableClauseQueue] **/
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator): MutableClauseQueue = of(unificator, emptyList())

        /** Creates a [MutableClauseQueue] with given clauses */
        @JvmStatic
        @JsName("of")
        fun of(unificator: Unificator, vararg clause: Clause): MutableClauseQueue = of(unificator, clause.asIterable())

        /** Let developers easily create a [MutableClauseQueue] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("ofScopes")
        fun of(unificator: Unificator, vararg clause: Scope.() -> Clause): MutableClauseQueue =
            of(
                unificator,
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [MutableClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("ofSequence")
        fun of(unificator: Unificator, clauses: Sequence<Clause>): MutableClauseQueue =
            of(unificator, clauses.asIterable())

        /** Creates a [MutableClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("ofIterable")
        fun of(unificator: Unificator, clauses: Iterable<Clause>): MutableClauseQueue =
            MutableReteClauseQueue(unificator, clauses)

        @JvmStatic
        @JsName("areEquals")
        fun equals(queue1: MutableClauseQueue, queue2: MutableClauseQueue): Boolean {
            return ClauseQueue.equals(queue1, queue2)
        }

        @JvmStatic
        @JsName("computeHashCode")
        fun hashCode(queue: MutableClauseQueue): Int {
            return itemWiseHashCode(
                MutableClauseQueue::class,
                itemWiseHashCode(queue)
            )
        }
    }
}
