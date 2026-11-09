package it.unibo.tuprolog.solve.search

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.js.JsName

/**
 * Orders the candidate rules of a call (#1010), when passed via
 * [it.unibo.tuprolog.solve.SolveOptions.setRuleSelection]; the default is source order.
 *
 * [candidates] are the rules whose head may match [goal], lazily produced: consuming them doesn't run them. The policy
 * may reorder them (reordering an unbounded sequence can't be lazy), or filter them, which makes the search incomplete.
 * Changing the order of clauses changes the order of answers, and can change termination and side effects.
 */
fun interface RuleSelectionPolicy {
    /** The rules to try for [goal], in order, among [candidates]. */
    @JsName("order")
    fun order(
        goal: Struct,
        candidates: Sequence<Rule>,
        context: ExecutionContext,
    ): Sequence<Rule>
}
