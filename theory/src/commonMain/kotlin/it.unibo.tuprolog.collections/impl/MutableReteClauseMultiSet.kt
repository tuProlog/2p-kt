package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractClauseCollection
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class MutableReteClauseMultiSet private constructor(
    private val rete: ReteNode<*, Clause>
) : MutableClauseMultiSet, AbstractClauseCollection(rete) {

    /** Construct a [MutableReteClauseMultiSet] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun add(clause: Clause): MutableClauseMultiSet {
        rete.put(clause)
        return this
    }

    override fun addAll(clauses: Iterable<Clause>): MutableClauseMultiSet {
        rete.put(clauses)
        return this
    }

    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseMultiSet> {
        val retracted = rete.remove(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    this, retracted.toList()
                )
        }    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseMultiSet> {
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

    override fun count(clause: Clause): Long =
        rete.indexedElements.count().toLong()


    override fun get(clause: Clause): Sequence<Clause> =
        rete.get(clause)

}