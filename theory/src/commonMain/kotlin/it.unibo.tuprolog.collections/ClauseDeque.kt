package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseDeque : ClauseCollection {
    fun addFirst(clause: Clause): ClauseDeque

    fun addLast(clause: Clause): ClauseDeque

    override fun add(clause: Clause): ClauseDeque =
        addLast(clause)

    override fun addAll(clause: Iterable<Clause>): ClauseDeque

    override fun retrieve(clause: Clause): RetrieveResult<out ClauseDeque>

    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseDeque>
}

