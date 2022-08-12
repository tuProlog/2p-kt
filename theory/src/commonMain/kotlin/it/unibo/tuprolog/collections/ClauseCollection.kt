package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface ClauseCollection : Iterable<Clause> {

    /** Computes the size of the [ClauseCollection] **/
    @JsName("size")
    val size: Int

    /** Tells if the [ClauseCollection] contains any [Clause] **/
    @JsName("isEmpty")
    fun isEmpty(): Boolean

    @JsName("isNonEmpty")
    fun isNonEmpty(): Boolean

    /** Tells if the [ClauseCollection] contains the given [Clause] **/
    @JsName("contains")
    operator fun contains(element: Clause): Boolean

    /** Tells if the [ClauseCollection] contains all the given [Clause] **/
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

    override fun iterator(): Iterator<Clause>

    companion object {

        /** Creates an empty [ClauseMultiSet] **/
        @JvmStatic
        @JsName("emptyMultiSet")
        fun emptyMultiSet(): ClauseMultiSet = multiSetOf(emptyList())

        /** Creates a [ClauseMultiSet] with given clauses */
        @JvmStatic
        @JsName("multiSetOf")
        fun multiSetOf(vararg clause: Clause): ClauseMultiSet = multiSetOf(clause.asIterable())

        /** Let developers easily create a [ClauseMultiSet] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("multiSetOfScopes")
        fun multiSetOf(vararg clause: Scope.() -> Clause): ClauseMultiSet =
            multiSetOf(
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [ClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfSequence")
        fun multiSetOf(clauses: Sequence<Clause>): ClauseMultiSet = multiSetOf(clauses.asIterable())

        /** Creates a [ClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfIterable")
        fun multiSetOf(clauses: Iterable<Clause>): ClauseMultiSet =
            ClauseMultiSet.of(clauses)

        /** Creates an empty [ClauseQueue] **/
        @JvmStatic
        @JsName("emptyQueue")
        fun emptyQueue(): ClauseQueue = queueOf(emptyList())

        /** Creates a [ClauseQueue] with given clauses */
        @JvmStatic
        @JsName("queueOf")
        fun queueOf(vararg clause: Clause): ClauseQueue = queueOf(clause.asIterable())

        /** Let developers easily create a [ClauseQueue] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("queueOfScopes")
        fun queueOf(vararg clause: Scope.() -> Clause): ClauseQueue =
            queueOf(
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [ClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("queueOfSequence")
        fun queueOf(clauses: Sequence<Clause>): ClauseQueue = queueOf(clauses.asIterable())

        /** Creates a [ClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("queueOfIterable")
        fun queueOf(clauses: Iterable<Clause>): ClauseQueue =
            ClauseQueue.of(clauses)
    }
}
