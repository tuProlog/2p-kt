package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils

internal abstract class AbstractMutableReteClauseCollection<Self : AbstractMutableReteClauseCollection<Self>>
protected constructor(
    rete: ReteTree
) : MutableClauseCollection, AbstractClauseCollection<Self>(rete) {

    override fun add(clause: Clause): Self {
        rete.assertZ(clause)
        @Suppress("UNCHECKED_CAST")
        return this as Self
    }

    override fun addAll(clauses: Iterable<Clause>): Self {
        clauses.forEach { rete.assertZ(it) }
        @Suppress("UNCHECKED_CAST")
        return this as Self
    }

    override fun retrieve(clause: Clause): RetrieveResult<out Self> {
        val retracted = rete.retractFirst(clause)

        @Suppress("UNCHECKED_CAST")
        return when {
            retracted.none() ->
                RetrieveResult.Failure(this as Self)
            else ->
                RetrieveResult.Success(
                    this as Self,
                    retracted.toList()
                )
        }
    }

    override fun retrieveAll(clause: Clause): RetrieveResult<out Self> {
        val retracted = rete.retractAll(clause)

        @Suppress("UNCHECKED_CAST")
        return when {
            retracted.none() ->
                RetrieveResult.Failure(this as Self)
            else ->
                RetrieveResult.Success(
                    this as Self,
                    retracted.toList()
                )
        }
    }
}