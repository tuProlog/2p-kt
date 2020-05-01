package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseMultiset : ClauseCollection {

    fun count(clause: Clause): Long

    override fun assert(clause: Clause): ClauseMultiset

    override fun assertAll(clause: Iterable<Clause>): ClauseMultiset

    override fun retract(clause: Clause): RetractResult<out ClauseMultiset>

    override fun retractAll(clause: Clause): RetractResult<out ClauseMultiset>
}

