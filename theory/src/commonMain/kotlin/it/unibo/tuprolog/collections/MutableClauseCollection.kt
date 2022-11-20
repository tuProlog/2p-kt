package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.unify.Unificator
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
        fun emptyMultiSet(unificator: Unificator): MutableClauseMultiSet =
            multiSetOf(unificator, emptyList())

        /** Creates a [MutableClauseMultiSet] with given clauses */
        @JvmStatic
        @JsName("multiSetOf")
        fun multiSetOf(unificator: Unificator, vararg clause: Clause): MutableClauseMultiSet =
            multiSetOf(unificator, clause.asIterable())

        /** Let developers easily create a [MutableClauseMultiSet] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("multiSetOfScopes")
        fun multiSetOf(unificator: Unificator, vararg clause: Scope.() -> Clause): MutableClauseMultiSet =
            multiSetOf(
                unificator,
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [MutableClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfSequence")
        fun multiSetOf(unificator: Unificator, clauses: Sequence<Clause>): MutableClauseMultiSet =
            multiSetOf(unificator, clauses.asIterable())

        /** Creates a [MutableClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("multiSetOfIterable")
        fun multiSetOf(unificator: Unificator, clauses: Iterable<Clause>): MutableClauseMultiSet =
            MutableClauseMultiSet.of(unificator, clauses)

        /** Creates an empty [MutableClauseQueue] **/
        @JvmStatic
        @JsName("emptyQueue")
        fun emptyQueue(unificator: Unificator): MutableClauseQueue =
            queueOf(unificator, emptyList())

        /** Creates a [MutableClauseQueue] with given clauses */
        @JvmStatic
        @JsName("queueOf")
        fun queueOf(unificator: Unificator, vararg clause: Clause): MutableClauseQueue =
            queueOf(unificator, clause.asIterable())

        /** Let developers easily create a [MutableClauseQueue] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("queueOfScopes")
        fun queueOf(unificator: Unificator, vararg clause: Scope.() -> Clause): MutableClauseQueue =
            queueOf(
                unificator,
                clause.map {
                    Scope.empty(it)
                }
            )

        /** Creates a [MutableClauseQueue] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("queueOfSequence")
        fun queueOf(unificator: Unificator, clauses: Sequence<Clause>): MutableClauseQueue =
            queueOf(unificator, clauses.asIterable())

        /** Creates a [MutableClauseQueue] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("queueOfIterable")
        fun queueOf(unificator: Unificator, clauses: Iterable<Clause>): MutableClauseQueue =
            MutableClauseQueue.of(unificator, clauses)
    }
}
