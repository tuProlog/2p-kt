package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractClauseCollection
import it.unibo.tuprolog.collections.ClauseMultiset
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class ReteClauseMultiSet private constructor(
    private val rete: ReteNode<*, Clause>
) : ClauseMultiset, AbstractClauseCollection(rete) {

    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun count(clause: Clause): Long {
        TODO("Not yet implemented")
    }

    override fun get(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun add(clause: Clause): ReteClauseMultiSet {
        TODO("Not yet implemented")
    }

    override fun addAll(clause: Iterable<Clause>): ReteClauseMultiSet {
        TODO("Not yet implemented")
    }

    override fun retrieve(clause: Clause): RetrieveResult<out ReteClauseMultiSet> {
        TODO("Not yet implemented")
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out ReteClauseMultiSet> {
        TODO("Not yet implemented")
    }

}