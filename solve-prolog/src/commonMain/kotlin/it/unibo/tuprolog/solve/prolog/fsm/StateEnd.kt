package it.unibo.tuprolog.solve.prolog.fsm

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext

/**
 * "End": a *resumable* terminal -- a solution (positive or negative) has just been emitted, but if
 * [PrologExecutionContext.hasOpenAlternatives] is true, [computeNext] loops back into `StateBacktracking`
 * rather than truly stopping. This is what makes `Solver.solve`'s lazy [Sequence] of
 * [it.unibo.tuprolog.solve.Solution]s work: pulling the next item from the sequence is exactly what triggers
 * this transition. Never times out ([isTimeout] is always `false`), since it is reached only after a solution
 * has already been produced. Each solution gets the full time budget: resuming the search for the next one restarts
 * the resolution's chronometer (see [it.unibo.tuprolog.utils.time.Chronometer.restart]), so the time the caller
 * spends between solutions is never counted.
 */
data class StateEnd(
    override val solution: Solution,
    override val context: PrologExecutionContext,
) : AbstractEndState(solution, context) {
    override val isTimeout: Boolean
        get() = false

    override fun computeNext(): State =
        if (context.hasOpenAlternatives) {
            context.chronometer.restart()
            StateBacktracking(context.copy(step = nextStep()))
        } else {
            super.computeNext()
        }

    override fun clone(context: PrologExecutionContext): StateEnd = copy(solution = solution, context = context)
}
