package it.unibo.tuprolog.solve.problogclassic.knowledge

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

/**
 * Represents a Probabilistic rule as intended in Problog annotation, which is a logic [Rule] with an additional
 * numeric attribute representing its probability of being true.
 *
 * @author Jason Dellaluce
 * */
open class ProblogRule(
    open val id: Long,
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

    override fun compareTo(other: Term): Int {
        if (other is ProblogRule) {
            var res = this.id.compareTo(other.id)
            if (res == 0) {
                res = head.compareTo(other.head)
            }
            return res
        }
        return super.compareTo(other)
    }
}
