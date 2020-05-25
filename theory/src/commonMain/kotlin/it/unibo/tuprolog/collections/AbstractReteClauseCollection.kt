package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal abstract class AbstractReteClauseCollection<Self : AbstractReteClauseCollection<Self>>
protected constructor(
    private val rete: ReteTree
) : AbstractClauseCollection<Self>(rete) {

    constructor(
        clauses: Iterable<Clause>,
        reteNodeBuilder: (Iterable<Clause>) -> ReteTree
    ) : this(reteNodeBuilder(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    protected abstract fun newCollectionBuilder(rete: ReteTree): Self

    override fun add(clause: Clause): Self =
        newCollectionBuilder(
            rete.deepCopy().apply {
                assertZ(Theory.checkClauseCorrect(clause))
            }
        )

    override fun addAll(clauses: Iterable<Clause>): Self =
        newCollectionBuilder(
            rete.deepCopy().apply {
                clauses.forEach {
                    assertZ(Theory.checkClauseCorrect(it))
                }
            }
        )

    override fun retrieve(clause: Clause): RetrieveResult<out Self> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.retractFirst(clause)

        @Suppress("UNCHECKED_CAST")
        return when {
            retracted.none() ->
                RetrieveResult.Failure(this as Self)
            else ->
                RetrieveResult.Success(
                    newCollectionBuilder(newTheory), retracted.toList()
                )
        }
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out Self> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.retractAll(clause)

        @Suppress("UNCHECKED_CAST")
        return when {
            retracted.none() ->
                RetrieveResult.Failure(this as Self)
            else ->
                RetrieveResult.Success(
                    newCollectionBuilder(newTheory), retracted.toList()
                )
        }
    }
}