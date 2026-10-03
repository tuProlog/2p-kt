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
    override val help: String =
        """
        `(+First, +Second)`

        Conjunction: proves `First` and then `Second`, using each solution of `First` to prove `Second`. In this concurrent solver conjunction is not itself a source of parallelism: the conjuncts are proved in sequence, while the alternative solutions of each conjunct may be explored concurrently.

        **Examples**

        ```prolog
        ?- X = 1, Y is X + 1.
        X = 1, Y = 2.

        % solutions may come in any order: collect them to compare
        % solutions may come in any order on this engine
        ?- findall(X, (member(X, [1, 2, 3]), X > 1), L), member(2, L), member(3, L), \+ member(1, L).
        yes.

        ?- X = 1, X = 2.
        no.
        ```
        """.trimIndent()

    override val Scope.head: List<Term>
        get() = listOf(varOf("A"), varOf("B"))

    override val Scope.body: Term
        get() = tupleOf(varOf("A"), varOf("B"))
}
