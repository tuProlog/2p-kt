package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractReteClauseCollection
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class ReteClauseMultiSet private constructor(
    private val rete: ReteNode<*, Clause>
) : ClauseMultiSet, AbstractReteClauseCollection<ReteClauseMultiSet>(rete) {

    /** Construct a [ReteClauseMultiSet] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun count(clause: Clause): Long =
        rete.get(clause).count().toLong()

    override fun get(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun newCollectionBuilder(rete: ReteNode<*, Clause>): ReteClauseMultiSet {
        return ReteClauseMultiSet(rete)
    }

}