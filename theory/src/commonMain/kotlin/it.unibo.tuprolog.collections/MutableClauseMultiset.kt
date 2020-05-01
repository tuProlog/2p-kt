package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseMultiset : ClauseMultiset {
    override fun assert(clause: Clause): MutableClauseMultiset

    override fun assertAll(clause: Iterable<Clause>): MutableClauseMultiset

    override fun retract(clause: Clause): RetractResult<out MutableClauseMultiset>

    override fun retractAll(clause: Clause): RetractResult<out MutableClauseMultiset>
}