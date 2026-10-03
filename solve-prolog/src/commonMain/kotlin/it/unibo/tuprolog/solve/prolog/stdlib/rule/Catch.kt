package it.unibo.tuprolog.solve.prolog.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList

/**
 * ISO `catch/3`: `catch(Goal, Catcher, Recovery) :- Goal.` -- the body just proves `Goal`; the actual
 * catch/recovery behaviour is not implemented here but in `StateException`, which climbs the execution-context
 * stack looking for a currently-executing goal shaped like this one whose `Catcher` unifies with a raised
 * exception, and if so proves `Recovery` instead.
 */
object Catch : RuleWrapper<PrologExecutionContext>("catch", 3) {
    override val help: String =
        """
        `catch(+Goal, ?Catcher, +Recovery)`

        Calls `Goal`, behaving exactly like `call/1` (including backtracking into `Goal`) as long as no exception is raised. If proving `Goal` raises an exception, via `throw/1` or a built-in error, whose ball unifies with `Catcher`, the bindings and choice points created by `Goal` are discarded and `Recovery` is called instead; otherwise the exception propagates to the outer `catch/3` calls.
        """.trimIndent()

    override val Scope.head: KtList<Term>
        get() = listOf(varOf("G"), varOf("E"), varOf("C"))

    override val Scope.body: Term
        get() = varOf("G")
}
