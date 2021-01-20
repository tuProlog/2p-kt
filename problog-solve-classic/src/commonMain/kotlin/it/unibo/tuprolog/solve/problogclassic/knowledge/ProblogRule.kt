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
    private val rule: Rule
) : Rule by rule {

    constructor(
        id: Long,
        probability: Double,
        head: Struct,
        vararg body: Term
    ) : this(id, probability, Rule.of(head, *body))

    override fun freshCopy(): ProblogRule = ProblogRule(id, probability, rule.freshCopy())

    override fun freshCopy(scope: Scope): ProblogRule = ProblogRule(id, probability, rule.freshCopy(scope))

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
