package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseCollection : Iterable<Clause> {
    val size: Int

    fun isEmpty(): Boolean

    operator fun contains(element: Clause): Boolean

    fun containsAll(element: Iterable<Clause>): Boolean

    fun assert(clause: Clause): ClauseCollection

    fun assertAll(clause: Iterable<Clause>): ClauseCollection

    fun retract(clause: Clause): RetractResult<out ClauseCollection>

    fun retractAll(clause: Clause): RetractResult<out ClauseCollection>
}

