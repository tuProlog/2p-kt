package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseCollection : ClauseCollection {
    override fun assert(clause: Clause): MutableClauseCollection

    override fun assertAll(clause: Iterable<Clause>): MutableClauseCollection

    override fun retract(clause: Clause): RetractResult<out MutableClauseCollection>

    override fun retractAll(clause: Clause): RetractResult<out MutableClauseCollection>
}