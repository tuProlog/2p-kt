package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractClauseCollection
import it.unibo.tuprolog.collections.ClauseDeque
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class ReteClauseDeque private constructor(
    private val rete: ReteNode<*, Clause>
) : ClauseDeque, AbstractClauseCollection(rete) {

    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun addFirst(clause: Clause): ClauseDeque {
        TODO("Not yet implemented")
    }

    override fun addLast(clause: Clause): ClauseDeque {
        TODO("Not yet implemented")
    }

    override fun getFirst(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun getLast(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun add(clause: Clause): ReteClauseDeque {
        TODO("Not yet implemented")
    }

    override fun addAll(clause: Iterable<Clause>): ReteClauseDeque {
        TODO("Not yet implemented")
    }

    override fun retrieveFirst(clause: Clause): RetrieveResult<out ClauseDeque> {
        TODO("Not yet implemented")
    }

    override fun retrieveLast(clause: Clause): RetrieveResult<out ClauseDeque> {
        TODO("Not yet implemented")
    }

    override fun retrieve(clause: Clause): RetrieveResult<out ReteClauseDeque> {
        TODO("Not yet implemented")
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out ReteClauseDeque> {
        TODO("Not yet implemented")
    }

}