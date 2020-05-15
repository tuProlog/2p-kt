package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseCollection : ClauseCollection {

    /** Adds the given [Clause] to this [MutableClauseCollection]. **/
    override fun add(clause: Clause): MutableClauseCollection

    /** Adds all the given [Clause] to this [MutableClauseCollection] **/
    override fun addAll(clauses: Iterable<Clause>): MutableClauseCollection

    /** Retrieves the first occurrence of the given [Clause] from this [MutableClauseCollection] as a [RetrieveResult] **/
    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseCollection>

    /** Retrieves all the occurrences of the given [Clause] from this [MutableClauseCollection] as a [RetrieveResult] **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseCollection>
}