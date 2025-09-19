package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause

internal abstract class AbstractMutableReteClauseCollection<Self : AbstractMutableReteClauseCollection<Self>> :
    MutableClauseCollection,
    AbstractClauseCollection<Self> {
    @Suppress("ConvertSecondaryConstructorToPrimary")
    protected constructor(rete: ReteTree) : super(rete)

    override fun add(clause: Clause): Self {
        rete.assertZ(clause)
        return self
    }

    override fun addAll(clauses: Iterable<Clause>): Self {
        clauses.forEach { rete.assertZ(it) }
        return self
    }

    override fun retrieve(clause: Clause): RetrieveResult<out Self> {
        val retracted = rete.retractFirst(clause)
        return when {
            retracted.none() -> RetrieveResult.Failure(self)
            else -> RetrieveResult.Success(self, retracted.toList())
        }
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out Self> {
        val retracted = rete.retractAll(clause)
        return when {
            retracted.none() -> RetrieveResult.Failure(self)
            else -> RetrieveResult.Success(self, retracted.toList())
        }
    }
}
