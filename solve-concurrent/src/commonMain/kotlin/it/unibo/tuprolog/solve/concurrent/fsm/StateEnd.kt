package it.unibo.tuprolog.solve.concurrent.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext

data class StateEnd(
    override val solution: Solution,
    override val context: ConcurrentExecutionContext,
) : EndState {
    override fun clone(context: ConcurrentExecutionContext): StateEnd = copy(solution = solution, context = context)
}
