package it.unibo.tuprolog.solve.prolog.machine

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Substitution

/** A candidate [rule], renamed apart, with the [mgu] of its head and the goal, for unificators that are not incremental. */
internal class MatchedRule(
    val rule: Rule,
    val mgu: Substitution.Unifier,
) : Rule by rule {
    override fun equals(other: Any?): Boolean = rule == other

    override fun hashCode(): Int = rule.hashCode()

    override fun toString(): String = rule.toString()
}
