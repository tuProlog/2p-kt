package it.unibo.tuprolog.solve.classic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext

data class StateEnd(
    override val solution: Solution,
    override val context: ClassicExecutionContext,
) : AbstractEndState(solution, context) {
    override val isTimeout: Boolean
        get() = false

    override fun computeNext(): State =
        if (context.hasOpenAlternatives) {
            StateBacktracking(context.copy(step = nextStep(), startTime = currentTime()))
        } else {
            super.computeNext()
        }

    override fun clone(context: ClassicExecutionContext): StateEnd = copy(solution = solution, context = context)
}
