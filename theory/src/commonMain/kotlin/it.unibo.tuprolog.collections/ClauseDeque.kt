package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseDeque : ClauseCollection {
    fun assertA(clause: Clause): ClauseDeque

    fun assertZ(clause: Clause): ClauseDeque

    override fun assert(clause: Clause): ClauseDeque =
        assertZ(clause)

    override fun assertAll(clause: Iterable<Clause>): ClauseDeque

    override fun retract(clause: Clause): RetractResult<out ClauseDeque>

    override fun retractAll(clause: Clause): RetractResult<out ClauseDeque>
}

