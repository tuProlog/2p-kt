package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution

internal data class StateEnd(
    override val solution: Solution,
    override val context: ExecutionContextImpl
) : AbstractEndState(solution, context) {

    override fun computeNext(): State {
        return if (context.hasOpenAlternatives) {
            StateBacktracking(context.copy(step = nextStep(), startTime = executionTime))
        } else {
            super.computeNext()
        }
    }
}