package it.unibo.tuprolog.solve.problogimpl

import it.unibo.tuprolog.core.*

class ProblogFact(override val id: Int, override val probability: Double, override val head: Struct):
    Fact, ProblogRule(id, probability, head) {
    override fun freshCopy(): ProblogFact = ProblogFact(id, probability, head.freshCopy())

    override fun freshCopy(scope: Scope): ProblogFact = ProblogFact(id, probability, head.freshCopy())

    override fun compareTo(other: Term): Int {
        if (other is ProblogFact) {
            var res = this.id.compareTo(other.id)
            if (res == 0) {
                res = head.compareTo(other.head)
            }
            return res
        }
        return super<ProblogRule>.compareTo(other)
    }

    override val isFact: Boolean
        get() = true

    override val body: Term
        get() = Truth.TRUE

}