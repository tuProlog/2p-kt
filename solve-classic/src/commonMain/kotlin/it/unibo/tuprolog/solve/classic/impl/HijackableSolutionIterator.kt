package it.unibo.tuprolog.solve.classic.impl

import it.unibo.tuprolog.solve.classic.MutableSolutionIterator
import it.unibo.tuprolog.solve.classic.fsm.State

internal class HijackableSolutionIterator(
    state: State,
    private val hijackStateTransitionCallback: (State, State, Long) -> State,
    private val onStateTransitionCallback: (State, State, Long) -> Unit,
) : AbstractSolutionIterator(state),
    MutableSolutionIterator {
    override fun computeNextState(
        state: State,
        step: Long,
    ): State = hijackStateTransition(state, state.next(), step)

    override fun hijackStateTransition(
        source: State,
        destination: State,
        index: Long,
    ): State = hijackStateTransitionCallback(source, destination, index)

    override fun onStateTransition(
        source: State,
        destination: State,
        index: Long,
    ) = onStateTransitionCallback(source, destination, index)
}
