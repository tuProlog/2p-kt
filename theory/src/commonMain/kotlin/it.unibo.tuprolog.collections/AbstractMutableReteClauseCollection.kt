package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal abstract class AbstractMutableReteClauseCollection<Self : AbstractMutableReteClauseCollection<Self>>

    protected constructor(
        private val rete: ReteNode<*, Clause>
    ) : MutableClauseCollection, AbstractClauseCollection<Self>(rete) {

    /** Construct a [AbstractMutableReteClauseCollection] from given clauses */
    constructor(
        clauses: Iterable<Clause>,
        reteNodeBuilder: (Iterable<Clause>) -> ReteNode<*, Clause>
    ) : this(reteNodeBuilder(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun add(clause: Clause): Self {
        rete.put(clause)
        @Suppress("UNCHECKED_CAST")
        return this as Self
    }

    override fun addAll(clauses: Iterable<Clause>): Self {
        rete.put(clauses)
        @Suppress("UNCHECKED_CAST")
        return this as Self
    }

    override fun retrieve(clause: Clause): RetrieveResult<out Self> {
        val retracted = rete.remove(clause)

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
        val retracted = rete.removeAll(clause)

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