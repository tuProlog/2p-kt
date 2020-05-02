package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.ReteClauseDeque
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope

interface ClauseDeque : ClauseCollection {

    /** Gives a freshly produced [ClauseDeque] including the given [Clause] in the first position and the content
     *  of this one **/
    fun addFirst(clause: Clause): ClauseDeque

    /** Gives a freshly produced [ClauseDeque] including the given [Clause] in the last position and the content
     *  of this one **/
    fun addLast(clause: Clause): ClauseDeque

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause], scanning from data structure from
     *  the first position to the last one **/
    fun getFirst(clause: Clause): Sequence<Clause>

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause], scanning from data structure from
     *  the last position to the first **/
    fun getLast(clause: Clause): Sequence<Clause>

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause]. Analogous to [getFirst] **/
    operator fun get(clause: Clause): Sequence<Clause> =
        getFirst(clause)

    /** Gives a freshly produced [ClauseDeque] including the given [Clause] and the content of this one.
     *  Analogous to [addFirst] **/
    override fun add(clause: Clause): ClauseDeque =
        addLast(clause)

    /** Gives a freshly produced [ClauseDeque] including all the given [Clause] and the content of this one **/
    override fun addAll(clauses: Iterable<Clause>): ClauseDeque

    /** Retrieve the first [Clause] unifying the given one, searching from the first position **/
    fun retrieveFirst(clause: Clause): RetrieveResult<out ClauseDeque>

    /** Retrieve the first [Clause] unifying the given one, searching from the last position **/
    fun retrieveLast(clause: Clause): RetrieveResult<out ClauseDeque>

    /** Retrieve the first [Clause] unifying the given one. Analogous to [retrieveFirst] **/
    override fun retrieve(clause: Clause): RetrieveResult<out ClauseDeque> =
        retrieveFirst(clause)

    /** Retrieve all the [Clause] unifying the given one **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseDeque>

    companion object {

        /** Creates an empty [ClauseDeque] **/
        fun empty(): ClauseDeque = of(emptyList())

        /** Creates a [ClauseDeque] with given clauses */
        fun of(vararg clause: Clause): ClauseDeque = of(clause.asIterable())

        /** Let developers easily create a [ClauseDeque] programmatically while avoiding variables names clashing */
        fun of(vararg clause: Scope.() -> Clause): ClauseDeque =
            of(clause.map {
                Scope.empty(it)
            })

        /** Creates a [ClauseDeque] from the given [Sequence] of [Clause] */
        fun of(clauses: Sequence<Clause>): ClauseDeque = of(clauses.asIterable())

        /** Creates a [ClauseDeque] from the given [Iterable] of [Clause] */
        fun of(clauses: Iterable<Clause>): ClauseDeque =
            ReteClauseDeque(clauses)
    }

}

