package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils

internal abstract class AbstractReteClauseCollection<Self : AbstractReteClauseCollection<Self>> protected constructor(
    rete: ReteTree,
) : AbstractClauseCollection<Self>(rete) {
    protected abstract fun newCollectionBuilder(rete: ReteTree): Self

    override fun add(clause: Clause): Self =
        newCollectionBuilder(
            rete.deepCopy().apply {
                assertZ(TheoryUtils.checkClauseCorrect(clause))
            },
        )

    override fun addAll(clauses: Iterable<Clause>): Self =
        newCollectionBuilder(
            rete.deepCopy().apply {
                clauses.forEach {
                    assertZ(TheoryUtils.checkClauseCorrect(it))
                }
            },
        )

    override fun retrieve(clause: Clause): RetrieveResult<out Self> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.retractFirst(clause)
        return when {
            retracted.none() -> RetrieveResult.Failure(self)
            else -> RetrieveResult.Success(newCollectionBuilder(newTheory), retracted.toList())
        }
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out Self> {
        val newTheory = rete.deepCopy()
        val retracted = newTheory.retractAll(clause)
        return when {
            retracted.none() -> RetrieveResult.Failure(self)
            else -> RetrieveResult.Success(newCollectionBuilder(newTheory), retracted.toList())
        }
    }
}
