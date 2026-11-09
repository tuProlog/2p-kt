package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Substitution

/**
 * A [rule], renamed with fresh variables, selected for the current goal together with their most general unifier
 * ([mgu]), computed once at selection (#1019), so that rule execution does not unify them again.
 */
internal class MatchedRule(
    val rule: Rule,
    val mgu: Substitution.Unifier,
) : Rule by rule {
    override fun equals(other: Any?): Boolean = rule == other

    override fun hashCode(): Int = rule.hashCode()

    override fun toString(): String = rule.toString()
}
