package it.unibo.tuprolog.solve.problogclassic.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.problogclassic.ProblogClassicExecutionContext

internal data class StateEnd(
    override val solution: Solution,
    override val context: ProblogClassicExecutionContext
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
