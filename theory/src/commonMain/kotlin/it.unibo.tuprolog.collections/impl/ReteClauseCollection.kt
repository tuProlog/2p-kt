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

    override fun add(clause: Clause): ReteClauseCollection =
        ReteClauseCollection(
            rete.deepCopy().apply {
                put(Theory.checkClauseCorrect(clause))
            }
        )

    override fun addAll(clauses: Iterable<Clause>): ReteClauseCollection =
        ReteClauseCollection(
            rete.deepCopy().apply {
                put(Theory.checkClausesCorrect(clauses))
            }
        )

    override fun retrieve(clause: Clause): RetrieveResult<out ReteClauseCollection> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.remove(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    ReteClauseCollection(newTheory), retracted.toList()
                )
        }
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out ReteClauseCollection> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.removeAll(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    ReteClauseCollection(newTheory), retracted.toList()
                )
        }
    }
}