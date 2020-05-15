package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractReteClauseCollection
import it.unibo.tuprolog.collections.ClauseDeque
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class ReteClauseDeque private constructor(
    private val rete: ReteTree
) : ClauseDeque, AbstractReteClauseCollection<ReteClauseDeque>(rete) {

    /** Construct a [ReteClauseDeque] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.ordered(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun addFirst(clause: Clause): ReteClauseDeque =
        ReteClauseDeque(
            rete.deepCopy().apply {
                assertA(Theory.checkClauseCorrect(clause))
            }
        )

    override fun addLast(clause: Clause): ReteClauseDeque =
        super<AbstractReteClauseCollection>.add(clause)


    override fun getFirst(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun getLast(clause: Clause): Sequence<Clause> =
        getFirst(clause).toList().asReversed().asSequence()

    override fun add(clause: Clause): ReteClauseDeque =
        addLast(clause)

    override fun retrieve(clause: Clause): RetrieveResult<out ReteClauseDeque> =
        super<AbstractReteClauseCollection>.retrieve(clause)

    override fun retrieveFirst(clause: Clause): RetrieveResult<out ReteClauseDeque> =
        super<AbstractReteClauseCollection>.retrieve(clause)

    override fun newCollectionBuilder(rete: ReteTree): ReteClauseDeque =
        ReteClauseDeque(rete)

}