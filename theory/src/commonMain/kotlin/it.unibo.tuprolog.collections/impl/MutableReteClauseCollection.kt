package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractClauseCollection
import it.unibo.tuprolog.collections.MutableClauseCollection
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class MutableReteClauseCollection private constructor(
    private val rete: ReteNode<*, Clause>
) : MutableClauseCollection, AbstractClauseCollection(rete) {

    /** Construct a [MutableReteClauseCollection] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun add(clause: Clause): MutableClauseCollection {
        rete.put(clause)
        return this
    }

    override fun addAll(clauses: Iterable<Clause>): MutableClauseCollection {
        rete.put(clauses)
        return this
    }

    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseCollection> {
        val retracted = rete.remove(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    this, retracted.toList()
                )
        }
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseCollection> {
        val retracted = rete.removeAll(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    this, retracted.toList()
                )
        }
    }
}