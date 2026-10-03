package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * The cut operator (`!/0`) -- currently registered as a plain rule with no body override, which means its body
 * defaults to [RuleWrapper]'s `true`: __`!` succeeds without pruning any choice point or alternative branch__.
 * `:solve-concurrent` has no choice-point stack to prune in the first place (see
 * [it.unibo.tuprolog.solve.concurrent.fsm.StateRuleSelection]'s "Cut caveat"), so this is currently a placeholder
 * rather than an ISO-conformant cut -- a comment on this object's registration in
 * [it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins] notes it may need converting into a primitive
 * ("smarter behaviour") in the future.
 */
object Cut : RuleWrapper<ExecutionContext>("!", 0) {
    override val help: String =
        """
        `!`

        Cut. In this concurrent solver, `!` currently succeeds without pruning anything: alternatives are explored in parallel, and there is no choice-point stack to cut. Programs relying on cut to discard solutions may therefore produce extra solutions here.

        **Examples**

        ```prolog
        ?- !.
        yes.

        % cut prunes nothing here: every alternative is still found
        % nothing is pruned: all three solutions survive the cut, in any order
        ?- findall(X, (member(X, [a, b, c]), !), L), member(a, L), member(b, L), member(c, L).
        yes.
        ```
        """.trimIndent()
}
