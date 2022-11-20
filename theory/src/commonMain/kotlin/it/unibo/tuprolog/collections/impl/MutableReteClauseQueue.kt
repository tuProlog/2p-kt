package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractMutableReteClauseCollection
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils
import it.unibo.tuprolog.unify.Unificator

internal class MutableReteClauseQueue private constructor(
    rete: ReteTree
) : MutableClauseQueue, AbstractMutableReteClauseCollection<MutableReteClauseQueue>(rete) {

    init {
        require(rete.isOrdered)
    }

    /** Construct a [MutableReteClauseQueue] from given clauses */
    constructor(unificator: Unificator, clauses: Iterable<Clause>) : this(ReteTree.ordered(unificator, clauses)) {
        TheoryUtils.checkClausesCorrect(clauses)
    }

    override fun getFifoOrdered(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun getLifoOrdered(clause: Clause): Sequence<Clause> =
        getFifoOrdered(clause).toList().asReversed().asSequence()

    override fun addFirst(clause: Clause): MutableReteClauseQueue {
        rete.assertA(clause)
        return this
    }

    override fun addLast(clause: Clause): MutableReteClauseQueue {
        rete.assertZ(clause)
        return this
    }

    override fun retrieveFirst(clause: Clause): RetrieveResult<out MutableReteClauseQueue> {
        val retracted = rete.retractFirst(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    this,
                    retracted.toList()
                )
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is MutableClauseQueue) {
            MutableClauseQueue.equals(this, other)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return MutableClauseQueue.hashCode(this)
    }

    override val self: MutableReteClauseQueue
        get() = this
}
