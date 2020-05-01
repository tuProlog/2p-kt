package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseMultiset : ClauseMultiset {
    override fun add(clause: Clause): MutableClauseMultiset

    override fun addAll(clause: Iterable<Clause>): MutableClauseMultiset

    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseMultiset>

    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseMultiset>
}