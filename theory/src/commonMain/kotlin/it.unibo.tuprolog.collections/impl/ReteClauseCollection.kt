package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractClauseCollection
import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class ReteClauseCollection private constructor(
    private val rete: ReteNode<*, Clause>
) : AbstractClauseCollection(rete) {

    /** Construct a Clause database from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun add(clause: Clause): ClauseCollection {
        TODO("Not yet implemented")
    }

    override fun addAll(clause: Iterable<Clause>): ClauseCollection {
        TODO("Not yet implemented")
    }

    override fun retrieve(clause: Clause): RetrieveResult<out ClauseCollection> {
        TODO("Not yet implemented")
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseCollection> {
        TODO("Not yet implemented")
    }


}