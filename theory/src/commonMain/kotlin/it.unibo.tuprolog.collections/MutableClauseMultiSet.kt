package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseMultiSet : ClauseMultiSet {

    /** Adds a [Clause] to this [MutableClauseMultiSet] **/
    override fun add(clause: Clause): MutableClauseMultiSet

    /** Adds all the given [Clause] to this [MutableClauseMultiSet] **/
    override fun addAll(clauses: Iterable<Clause>): MutableClauseMultiSet

    /** Retrieves the first unifying [Clause] from this [MutableClauseMultiSet] as a [RetrieveResult]**/
    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseMultiSet>

    /** Retrieves all the unifying [Clause] from this [MutableClauseMultiSet] as a [RetrieveResult]**/
    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseMultiSet>
}