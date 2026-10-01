package it.unibo.tuprolog.solve.prolog.impl

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.prolog.SolutionIterator
import it.unibo.tuprolog.solve.prolog.fsm.State

internal abstract class AbstractSolutionIterator(
    state: State,
) : SolutionIterator {
    final override var state: State = state
        private set

    final override var step: Long = 0
        private set

    final override fun hasNext(): Boolean = state.let { !it.isEndState || it.castToEndState().hasOpenAlternatives }

    final override fun next(): Solution {
        do {
            val previousState = state
            state = computeNextState(state, ++step)
            onStateTransition(previousState, state, step)
        } while (!state.isEndState)
        return state.castToEndState().solution
    }

    protected abstract fun computeNextState(
        state: State,
        step: Long,
    ): State
}
