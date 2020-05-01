package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseDeque : ClauseDeque {
    override fun addFirst(clause: Clause): MutableClauseDeque

    override fun addLast(clause: Clause): MutableClauseDeque

    override fun add(clause: Clause): MutableClauseDeque

    override fun addAll(clause: Iterable<Clause>): MutableClauseDeque

    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseDeque>

    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseDeque>
}