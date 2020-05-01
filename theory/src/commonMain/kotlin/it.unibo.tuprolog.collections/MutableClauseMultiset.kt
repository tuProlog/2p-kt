package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseMultiset : ClauseMultiset {

    /** Adds a [Clause] to this [MutableClauseMultiset] **/
    override fun add(clause: Clause): MutableClauseMultiset

    /** Adds all the given [Clause] to this [MutableClauseMultiset] **/
    override fun addAll(clause: Iterable<Clause>): MutableClauseMultiset

    /** Retrieves the first unifying [Clause] from this [MutableClauseMultiset] as a [RetrieveResult]**/
    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseMultiset>

    /** Retrieves all the unifying [Clause] from this [MutableClauseMultiset] as a [RetrieveResult]**/
    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseMultiset>
}