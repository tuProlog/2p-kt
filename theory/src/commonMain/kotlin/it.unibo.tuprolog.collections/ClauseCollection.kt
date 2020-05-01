package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseCollection : Iterable<Clause> {

    /** Computes the size of the [ClauseCollection] **/
    val size: Int

    /** Tells if the [ClauseCollection] contains any [Clause] **/
    fun isEmpty(): Boolean

    /** Tells if the [ClauseCollection] contains the given [Clause] **/
    operator fun contains(element: Clause): Boolean

    /** Tells if the [ClauseCollection] contains all the given [Clause] **/
    fun containsAll(element: Iterable<Clause>): Boolean

    /** Gives a freshly produced [ClauseCollection] including the given [Clause] and the content of this one **/
    fun add(clause: Clause): ClauseCollection

    /** Gives a freshly produced [ClauseCollection] including all the given [Clause] and the content of this one **/
    fun addAll(clause: Iterable<Clause>): ClauseCollection

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting the given [Clause]
     *  from this [ClauseCollection] **/
    fun retrieve(clause: Clause): RetrieveResult<out ClauseCollection>

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting all the given [Clause]
     *  from this [ClauseCollection] **/
    fun retrieveAll(clause: Clause): RetrieveResult<out ClauseCollection>
}



