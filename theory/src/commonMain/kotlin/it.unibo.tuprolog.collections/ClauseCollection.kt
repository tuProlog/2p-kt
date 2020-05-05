package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope

interface ClauseCollection : Iterable<Clause> {

    /** Computes the size of the [ClauseCollection] **/
    val size: Int

    /** Tells if the [ClauseCollection] contains any [Clause] **/
    fun isEmpty(): Boolean

    /** Tells if the [ClauseCollection] contains the given [Clause] **/
    operator fun contains(element: Clause): Boolean

    /** Tells if the [ClauseCollection] contains all the given [Clause] **/
    fun containsAll(elements: Iterable<Clause>): Boolean

    /** Gives a freshly produced [ClauseCollection] including the given [Clause] and the content of this one **/
    fun add(clause: Clause): ClauseCollection

    /** Gives a freshly produced [ClauseCollection] including all the given [Clause] and the content of this one **/
    fun addAll(clauses: Iterable<Clause>): ClauseCollection

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting the given [Clause]
     *  from this [ClauseCollection] **/
    fun retrieve(clause: Clause): RetrieveResult<out ClauseCollection>

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting all the given [Clause]
     *  from this [ClauseCollection] **/
    fun retrieveAll(clause: Clause): RetrieveResult<out ClauseCollection>

    companion object {

        /** Creates an empty [ClauseCollection] **/
        fun empty(): ClauseCollection = of(emptyList())

        /** Creates a [ClauseCollection] with given clauses */
        fun of(vararg clause: Clause): ClauseCollection = of(clause.asIterable())

        /** Let developers easily create a [ClauseCollection] programmatically while avoiding variables names clashing */
        fun of(vararg clause: Scope.() -> Clause): ClauseCollection =
            of(clause.map {
                Scope.empty(it)
            })

        /** Creates a [ClauseCollection] from the given [Sequence] of [Clause] */
        fun of(clauses: Sequence<Clause>): ClauseCollection = of(clauses.asIterable())

        /** Creates a [ClauseCollection] from the given [Iterable] of [Clause] */
        fun of(clauses: Iterable<Clause>): ClauseCollection =
            ClauseMultiset.of(clauses)
    }
}



