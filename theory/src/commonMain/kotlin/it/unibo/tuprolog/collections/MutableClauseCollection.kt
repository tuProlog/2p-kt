package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface MutableClauseCollection : ClauseCollection {

    /** Adds the given [Clause] to this [MutableClauseCollection]. **/
    override fun add(clause: Clause): MutableClauseCollection

    /** Adds all the given [Clause] to this [MutableClauseCollection] **/
    override fun addAll(clauses: Iterable<Clause>): MutableClauseCollection

    /** Retrieves the first occurrence of the given [Clause] from this [MutableClauseCollection] as a [RetrieveResult] **/
    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseCollection>

    /** Retrieves all the occurrences of the given [Clause] from this [MutableClauseCollection] as a [RetrieveResult] **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseCollection>

    companion object {

        /** Creates an empty [MutableClauseMultiSet] **/
        @JvmStatic
        @JsName("emptyMultiSet")
        fun emptyMultiSet(): MutableClauseMultiSet = multiSetOf(emptyList())

        /** Creates a [MutableClauseMultiSet] with given clauses */
        @JvmStatic
        @JsName("multiSetOf")
        fun multiSetOf(vararg clause: Clause): MutableClauseMultiSet = multiSetOf(clause.asIterable())

        /** Let developers easily create a [MutableClauseMultiSet] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("multiSetOfScopes")
        fun multiSetOf(vararg clause: Scope.() -> Clause): MutableClauseMultiSet =
            multiSetOf(clause.map {
                Scope.empty(it)
            })

        /** Creates a [MutableClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfSequence")
        fun multiSetOf(clauses: Sequence<Clause>): MutableClauseMultiSet = multiSetOf(clauses.asIterable())

        /** Creates a [MutableClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfIterable")
        fun multiSetOf(clauses: Iterable<Clause>): MutableClauseMultiSet =
            MutableClauseMultiSet.of(clauses)

        /** Creates an empty [MutableClauseQueue] **/
        @JvmStatic
        @JsName("emptyQueue")
        fun emptyQueue(): MutableClauseQueue = queueOf(emptyList())

        /** Creates a [MutableClauseQueue] with given clauses */
        @JvmStatic
        @JsName("queueOf")
        fun queueOf(vararg clause: Clause): MutableClauseQueue = queueOf(clause.asIterable())

        /** Let developers easily create a [MutableClauseQueue] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("queueOfScopes")
        fun queueOf(vararg clause: Scope.() -> Clause): MutableClauseQueue =
            queueOf(clause.map {
                Scope.empty(it)
            })

        /** Creates a [MutableClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("queueOfSequence")
        fun queueOf(clauses: Sequence<Clause>): MutableClauseQueue = queueOf(clauses.asIterable())

        /** Creates a [MutableClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("queueOfIterable")
        fun queueOf(clauses: Iterable<Clause>): MutableClauseQueue =
            MutableClauseQueue.of(clauses)
    }
}