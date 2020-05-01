package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseCollection : Iterable<Clause> {
    val size: Int

    fun isEmpty(): Boolean

    operator fun contains(element: Clause): Boolean

    fun containsAll(element: Iterable<Clause>): Boolean

    fun add(clause: Clause): ClauseCollection

    fun addAll(clause: Iterable<Clause>): ClauseCollection

    fun retrieve(clause: Clause): RetrieveResult<out ClauseCollection>

    fun retrieveAll(clause: Clause): RetrieveResult<out ClauseCollection>


}

