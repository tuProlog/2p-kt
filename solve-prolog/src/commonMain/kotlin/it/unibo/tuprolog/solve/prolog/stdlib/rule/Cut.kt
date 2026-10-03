package it.unibo.tuprolog.solve.prolog.stdlib.rule

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * ISO cut `'!'/0`. This [RuleWrapper] only registers the `!/0` signature as a defined predicate (its default,
 * unused, body is just `true`): `StateRuleSelection` recognises `!` via its own `isCut()` check before it ever
 * reaches ordinary rule resolution, and performs the actual pruning of the choice-point queue directly.
 */
object Cut : RuleWrapper<ExecutionContext>("!", 0) {
    override val help: String =
        """
        `!`

        Cut: always succeeds, committing to the choices made since the current clause was selected. The remaining alternative clauses of the predicate being proved, and the remaining alternatives of the goals at the left of `!` in the clause body, are discarded. Within `call/1`, `\+/1`, `findall/3` and similar meta-calls, cut is local to the called goal.

        **Examples**

        ```prolog
        ?- assertz((first(X) :- member(X, [a, b, c]), !)), findall(X, first(X), L).
        L = [a].

        ?- findall(X, (member(X, [a, b, c]), !), L).
        L = [a].

        ?- findall(X, (member(X, [a, b, c]), call(!)), L).
        L = [a, b, c].
        ```
        """.trimIndent()
}
