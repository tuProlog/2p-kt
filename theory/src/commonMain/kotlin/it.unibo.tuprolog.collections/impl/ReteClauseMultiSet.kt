package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractClauseCollection
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class ReteClauseMultiSet private constructor(
    private val rete: ReteNode<*, Clause>
) : ClauseMultiSet, AbstractClauseCollection(rete) {

    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun count(clause: Clause): Long =
        rete.get(clause).count().toLong()

    override fun get(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun add(clause: Clause): ReteClauseMultiSet =
        ReteClauseMultiSet(
            rete.deepCopy().apply {
                put(Theory.checkClauseCorrect(clause))
            }
        )

    override fun addAll(clauses: Iterable<Clause>): ReteClauseMultiSet =
        ReteClauseMultiSet(
            rete.deepCopy().apply {
                put(Theory.checkClausesCorrect(clauses))
            }
        )

    override fun retrieve(clause: Clause): RetrieveResult<out ReteClauseMultiSet> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.remove(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    ReteClauseMultiSet(newTheory), retracted.toList()
                )
        }
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out ReteClauseMultiSet> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.removeAll(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    ReteClauseMultiSet(newTheory), retracted.toList()
                )
        }
    }

}