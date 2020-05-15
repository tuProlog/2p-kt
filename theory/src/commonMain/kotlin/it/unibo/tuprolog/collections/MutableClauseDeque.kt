package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableReteClauseDeque
import it.unibo.tuprolog.collections.impl.ReteClauseDeque
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope

interface MutableClauseDeque : ClauseDeque {

    /** Adds the given [Clause] as the first element in this [MutableClauseDeque] **/
    override fun addFirst(clause: Clause): MutableClauseDeque

    /** Adds the given [Clause] as the last element in this [MutableClauseDeque] **/
    override fun addLast(clause: Clause): MutableClauseDeque

    /** Adds the given [Clause] to this [MutableClauseDeque]. Analogous to [addLast]**/
    override fun add(clause: Clause): MutableClauseDeque

    /** Adds all the given [Clause] to this [MutableClauseDeque] **/
    override fun addAll(clauses: Iterable<Clause>): MutableClauseDeque

    /** Retrieves the first [Clause] unifying the given one, searching from the first position **/
    override fun retrieveFirst(clause: Clause): RetrieveResult<out MutableClauseDeque>

    /** Retrieves the first [Clause] unifying the given one, searching from the last position **/
    override fun retrieveLast(clause: Clause): RetrieveResult<out MutableClauseDeque>

    /** Retrieves the first occurrence of the given [Clause] from this [MutableClauseDeque] as a [RetrieveResult] **/
    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseDeque>

    /** Retrieves all the occurrences of the given [Clause] from this [MutableClauseDeque] as a [RetrieveResult] **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseDeque>

    companion object {

        /** Creates an empty [MutableClauseDeque] **/
        fun empty(): MutableClauseDeque = of(emptyList())

        /** Creates a [MutableClauseDeque] with given clauses */
        fun of(vararg clause: Clause): MutableClauseDeque = of(clause.asIterable())

        /** Let developers easily create a [MutableClauseDeque] programmatically while avoiding variables names clashing */
        fun of(vararg clause: Scope.() -> Clause): MutableClauseDeque =
            of(clause.map {
                Scope.empty(it)
            })

        /** Creates a [MutableClauseDeque] from the given [Sequence] of [Clause] */
        fun of(clauses: Sequence<Clause>): MutableClauseDeque = of(clauses.asIterable())

        /** Creates a [MutableClauseDeque] from the given [Iterable] of [Clause] */
        fun of(clauses: Iterable<Clause>): MutableClauseDeque =
            MutableReteClauseDeque(clauses)
    }

}