package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractMutableReteClauseCollection
import it.unibo.tuprolog.collections.MutableClauseMultiSet
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils
import it.unibo.tuprolog.unify.Unificator

internal class MutableReteClauseMultiSet private constructor(
    rete: ReteTree
) : MutableClauseMultiSet, AbstractMutableReteClauseCollection<MutableReteClauseMultiSet>(rete) {

    init {
        require(!rete.isOrdered)
    }

    /** Construct a [MutableReteClauseMultiSet] from given clauses */
    constructor(unificator: Unificator, clauses: Iterable<Clause>) : this(ReteTree.unordered(unificator, clauses)) {
        TheoryUtils.checkClausesCorrect(clauses)
    }

    override fun count(clause: Clause): Long =
        rete.get(clause).count().toLong()

    override fun get(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun equals(other: Any?): Boolean {
        return if (other is MutableClauseMultiSet) {
            MutableClauseMultiSet.equals(this, other)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return MutableClauseMultiSet.hashCode(this)
    }

    override val self: MutableReteClauseMultiSet
        get() = this
}
