package it.unibo.tuprolog.solve.classic.impl

import it.unibo.tuprolog.solve.classic.fsm.State

internal class SimpleSolutionIterator(
    state: State,
    private val onStateTransitionCallback: (State, State, Long) -> Unit,
) : AbstractSolutionIterator(state) {
    override fun computeNextState(
        state: State,
        step: Long,
    ): State = state.next()

    override fun onStateTransition(
        source: State,
        destination: State,
        index: Long,
    ) = onStateTransitionCallback(source, destination, index)
}
