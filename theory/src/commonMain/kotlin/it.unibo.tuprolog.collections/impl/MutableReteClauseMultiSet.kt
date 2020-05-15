package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractMutableReteClauseCollection
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class MutableReteClauseMultiSet private constructor(
    private val rete: ReteNode<*, Clause>
) : MutableClauseMultiSet, AbstractMutableReteClauseCollection<MutableReteClauseMultiSet>(rete) {

    /** Construct a [MutableReteClauseMultiSet] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun count(clause: Clause): Long =
        rete.get(clause).count().toLong()


    override fun get(clause: Clause): Sequence<Clause> =
        rete.get(clause)

}