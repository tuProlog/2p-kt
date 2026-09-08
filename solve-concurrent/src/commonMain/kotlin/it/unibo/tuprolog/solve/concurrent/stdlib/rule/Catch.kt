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
    override val Scope.head: List<Term>
        get() = listOf(varOf("G"), varOf("E"), varOf("C"))

    override val Scope.body: Term
        get() = varOf("G")
}
