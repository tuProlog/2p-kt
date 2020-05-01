package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseDeque : ClauseDeque {
    override fun assertA(clause: Clause): MutableClauseDeque

    override fun assertZ(clause: Clause): MutableClauseDeque

    override fun assert(clause: Clause): MutableClauseDeque

    override fun assertAll(clause: Iterable<Clause>): MutableClauseDeque

    override fun retract(clause: Clause): RetractResult<out MutableClauseDeque>

    override fun retractAll(clause: Clause): RetractResult<out MutableClauseDeque>
}