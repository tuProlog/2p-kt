package it.unibo.tuprolog.solve.search

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
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

/**
 * Chooses which goal of the current conjunction runs next (#1010), when passed via
 * [it.unibo.tuprolog.solve.SolveOptions.setGoalSelection]; the default is the leftmost one.
 *
 * [goals] are the goals left in the current clause body (or query), with their current bindings; control constructs
 * (e.g. `;`/2, `->`/2, `call/N`, `catch/3`) count as one opaque goal each, whose inner goals are never offered here.
 * Choosing another goal than the leftmost one is an alternative execution semantics: it can change termination, errors,
 * cuts and side effects.
 */
fun interface GoalSelectionPolicy {
    /** The index, in [goals], of the goal to run next. */
    @JsName("select")
    fun select(
        goals: List<Term>,
        context: ExecutionContext,
    ): Int
}
