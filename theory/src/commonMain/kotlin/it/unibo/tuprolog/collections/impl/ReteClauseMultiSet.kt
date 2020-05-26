package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractReteClauseCollection
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils

internal class ReteClauseMultiSet private constructor(
    private val rete: ReteTree
) : ClauseMultiSet, AbstractReteClauseCollection<ReteClauseMultiSet>(rete) {

    /** Construct a [ReteClauseMultiSet] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.unordered(clauses)) {
        TheoryUtils.checkClausesCorrect(clauses)
    }

    override fun count(clause: Clause): Long =
        rete.get(clause).count().toLong()

    override fun get(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun newCollectionBuilder(rete: ReteTree): ReteClauseMultiSet {
        return ReteClauseMultiSet(rete)
    }

}