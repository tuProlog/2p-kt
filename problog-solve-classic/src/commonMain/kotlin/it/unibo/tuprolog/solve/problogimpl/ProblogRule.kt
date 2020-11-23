package it.unibo.tuprolog.solve.problogimpl

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

open class ProblogRule(
    open val id: Int,
    open val probability: Double,
    override val head: Struct,
    private vararg val b: Term
) : Rule by Rule.of(head, *b) {
    override fun freshCopy(): ProblogRule {
        val ruleCopy = super.freshCopy()
        return ProblogRule(id, probability, ruleCopy.head, ruleCopy.body)
    }

    override fun freshCopy(scope: Scope): ProblogRule {
        val ruleCopy = super.freshCopy(scope)
        return ProblogRule(id, probability, ruleCopy.head, ruleCopy.body)
    }
}
