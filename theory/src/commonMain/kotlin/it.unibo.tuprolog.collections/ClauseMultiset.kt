package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseMultiset : ClauseCollection {

    fun count(clause: Clause): Long

    override fun add(clause: Clause): ClauseMultiset

    override fun addAll(clause: Iterable<Clause>): ClauseMultiset

    override fun retrieve(clause: Clause): RetrieveResult<out ClauseMultiset>

    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseMultiset>
}

