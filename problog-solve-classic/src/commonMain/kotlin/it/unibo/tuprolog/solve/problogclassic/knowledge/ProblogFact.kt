package it.unibo.tuprolog.solve.problogclassic.knowledge

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth

/**
 * Represents a Probabilistic fact as intended in Problog annotation, which is a logic [Fact] with an additional
 * numeric attribute representing its probability of being true.
 *
 * @author Jason Dellaluce
 * */
class ProblogFact(override val id: Long, override val probability: Double, override val head: Struct) :
    Fact, ProblogRule(id, probability, head) {
    override fun freshCopy(): ProblogFact = ProblogFact(id, probability, head.freshCopy())

    override fun freshCopy(scope: Scope): ProblogFact = ProblogFact(id, probability, head.freshCopy(scope))

    override fun compareTo(other: Term): Int {
        return super<ProblogRule>.compareTo(other)
    }

    override val isFact: Boolean
        get() = true

    override val body: Term
        get() = Truth.TRUE
}
