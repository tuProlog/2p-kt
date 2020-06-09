package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext
import it.unibo.tuprolog.solve.Solution

internal data class StateEnd(
    override val solution: Solution,
    override val context: ClassicExecutionContext
) : AbstractEndState(solution, context) {

    override val isTimeout: Boolean
        get() = false

    override fun computeNext(): State {
        return if (context.hasOpenAlternatives) {
            StateBacktracking(context.copy(step = nextStep(), startTime = currentTime()))
        } else {
            super.computeNext()
        }
    }
}