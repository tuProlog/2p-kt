package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableReteClauseQueue
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope

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
        fun empty(): MutableClauseQueue = of(emptyList())

        /** Creates a [MutableClauseQueue] with given clauses */
        fun of(vararg clause: Clause): MutableClauseQueue = of(clause.asIterable())

        /** Let developers easily create a [MutableClauseQueue] programmatically while avoiding variables names clashing */
        fun of(vararg clause: Scope.() -> Clause): MutableClauseQueue =
            of(clause.map {
                Scope.empty(it)
            })

        /** Creates a [MutableClauseQueue] from the given [Sequence] of [Clause] */
        fun of(clauses: Sequence<Clause>): MutableClauseQueue = of(clauses.asIterable())

        /** Creates a [MutableClauseQueue] from the given [Iterable] of [Clause] */
        fun of(clauses: Iterable<Clause>): MutableClauseQueue =
            MutableReteClauseQueue(clauses)
    }

}