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

    override fun addFirst(clause: Clause): ReteClauseDeque =
        ReteClauseDeque(
            rete.deepCopy().apply {
                put(Theory.checkClauseCorrect(clause), beforeOthers = true)
            }
        )

    override fun addLast(clause: Clause): ReteClauseDeque =
        ReteClauseDeque(
            rete.deepCopy().apply {
                put(Theory.checkClauseCorrect(clause))
            }
        )

    override fun getFirst(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun getLast(clause: Clause): Sequence<Clause> =
        getFirst(clause).toList().asReversed().asSequence()

    override fun add(clause: Clause): ReteClauseDeque =
        addLast(clause)

    override fun addAll(clauses: Iterable<Clause>): ReteClauseDeque =
        ReteClauseDeque(
            rete.deepCopy().apply {
                put(Theory.checkClausesCorrect(clauses))
            }
        )

    override fun retrieveFirst(clause: Clause): RetrieveResult<out ReteClauseDeque> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.remove(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    ReteClauseDeque(newTheory), retracted.toList()
                )
        }
    }

    override fun retrieveLast(clause: Clause): RetrieveResult<out ReteClauseDeque> {
        TODO("Not yet implemented")
    }

    override fun retrieve(clause: Clause): RetrieveResult<out ReteClauseDeque> =
        retrieveFirst(clause)

    override fun retrieveAll(clause: Clause): RetrieveResult<out ReteClauseDeque> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.removeAll(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    ReteClauseDeque(newTheory), retracted.toList()
                )
        }
    }
}