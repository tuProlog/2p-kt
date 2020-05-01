package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface MutableClauseCollection : ClauseCollection {
    override fun add(clause: Clause): MutableClauseCollection

    override fun addAll(clause: Iterable<Clause>): MutableClauseCollection

    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseCollection>

    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseCollection>
}