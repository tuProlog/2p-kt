package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * `catch/3`: resolves as its first argument, `G`; the second (`E`, the catcher pattern) and third (`C`, the
 * recovery goal) arguments are never actually resolved as part of this rule's body -- it is
 * [it.unibo.tuprolog.solve.concurrent.fsm.StateException] that inspects `catch(G, E, C)` goals found while
 * walking [ConcurrentExecutionContext.pathToRoot], unifying `E` against a raised error and, on a match, resolving
 * `C` instead.
 */
object Catch : RuleWrapper<ConcurrentExecutionContext>("catch", 3) {
    override val help: String =
        """
        `catch(+Goal, ?Catcher, +Recovery)`

        Calls `Goal`, behaving exactly like `call/1` (including backtracking into `Goal`) as long as no exception is raised. If proving `Goal` raises an exception, via `throw/1` or a built-in error, whose ball unifies with `Catcher`, the bindings and choice points created by `Goal` are discarded and `Recovery` is called instead; otherwise the exception propagates to the outer `catch/3` calls.
        """.trimIndent()

    override val Scope.head: List<Term>
        get() = listOf(varOf("G"), varOf("E"), varOf("C"))

    override val Scope.body: Term
        get() = varOf("G")
}
