package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext

/**
 * The [EndState] a branch reaches upon successfully proving the root [ConcurrentExecutionContext.query] ([solution]
 * is a [Solution.Yes]) or exhausting every alternative without proving it ([solution] is a [Solution.No]).
 */
data class StateEnd(
    override val solution: Solution,
    override val context: ConcurrentExecutionContext,
) : EndState {
    override fun clone(context: ConcurrentExecutionContext): StateEnd = copy(solution = solution, context = context)
}
