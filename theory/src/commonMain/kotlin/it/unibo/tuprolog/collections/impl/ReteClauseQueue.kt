package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractReteClauseCollection
import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.MutableClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils
import it.unibo.tuprolog.unify.Unificator

internal class ReteClauseQueue private constructor(
    rete: ReteTree
) : ClauseQueue, AbstractReteClauseCollection<ReteClauseQueue>(rete) {

    private val hashCodeCache by lazy {
        ClauseQueue.hashCode(this)
    }

    init {
        require(rete.isOrdered)
    }

    /** Construct a [ReteClauseQueue] from given clauses */
    constructor(unificator: Unificator, clauses: Iterable<Clause>) : this(ReteTree.ordered(unificator, clauses)) {
        TheoryUtils.checkClausesCorrect(clauses)
    }

    override fun addFirst(clause: Clause): ReteClauseQueue =
        ReteClauseQueue(
            rete.deepCopy().apply {
                assertA(TheoryUtils.checkClauseCorrect(clause))
            }
        )

    override fun addLast(clause: Clause): ReteClauseQueue =
        ReteClauseQueue(
            rete.deepCopy().apply {
                assertZ(TheoryUtils.checkClauseCorrect(clause))
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

    override fun equals(other: Any?): Boolean {
        return if (other is ClauseQueue && other !is MutableClauseQueue) {
            ClauseQueue.equals(this, other)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return hashCodeCache
    }

    override val self: ReteClauseQueue
        get() = this
}
