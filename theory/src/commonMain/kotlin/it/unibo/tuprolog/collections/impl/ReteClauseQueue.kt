package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractReteClauseCollection
import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class ReteClauseQueue private constructor(
    private val rete: ReteTree
) : ClauseQueue, AbstractReteClauseCollection<ReteClauseQueue>(rete) {

    /** Construct a [ReteClauseQueue] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.ordered(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun addFirst(clause: Clause): ReteClauseQueue =
        ReteClauseQueue(
            rete.deepCopy().apply {
                assertA(Theory.checkClauseCorrect(clause))
            }
        )

    override fun addLast(clause: Clause): ReteClauseQueue =
        ReteClauseQueue(
            rete.deepCopy().apply {
                assertZ(Theory.checkClauseCorrect(clause))
            }
        )

    override fun getFifoOrdered(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun getLifoOrdered(clause: Clause): Sequence<Clause> =
        getFifoOrdered(clause).toList().asReversed().asSequence()

    override fun add(clause: Clause): ReteClauseQueue =
        addLast(clause)

    override fun retrieve(clause: Clause): RetrieveResult<out ReteClauseQueue> =
        super<AbstractReteClauseCollection>.retrieve(clause)

    override fun retrieveFirst(clause: Clause): RetrieveResult<out ReteClauseQueue> =
        super<AbstractReteClauseCollection>.retrieve(clause)

    override fun newCollectionBuilder(rete: ReteTree): ReteClauseQueue =
        ReteClauseQueue(rete)

}