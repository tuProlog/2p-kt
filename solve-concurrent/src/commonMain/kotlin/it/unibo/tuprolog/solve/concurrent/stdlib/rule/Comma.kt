package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * Conjunction (`,/2`): rewrites `(A, B)` into the tuple `A, B`, so it is unfolded into two separate goals by
 * [it.unibo.tuprolog.solve.concurrent.fsm.toGoals]/[it.unibo.tuprolog.solve.concurrent.fsm.unfoldGoals] and
 * resolved sequentially, within the same branch, by [it.unibo.tuprolog.solve.concurrent.fsm.StateGoalSelection].
 * Unlike [it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Or], conjunction is not itself a source of
 * concurrency: it is the *alternatives* at each conjunct's choice points that get explored in parallel, not the
 * two sides of a comma.
 */
object Comma : RuleWrapper<ConcurrentExecutionContext>(",", 2) {
    override val Scope.head: List<Term>
        get() = listOf(varOf("A"), varOf("B"))

    override val Scope.body: Term
        get() = tupleOf(varOf("A"), varOf("B"))
}
