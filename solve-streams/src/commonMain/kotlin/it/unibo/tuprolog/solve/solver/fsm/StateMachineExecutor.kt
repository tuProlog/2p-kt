package it.unibo.tuprolog.solve.solver.fsm

/**
 * A class that makes the state machine execute its behaviour
 *
 * @author Enrico
 */
internal object StateMachineExecutor {

    /**
     * Executes the state-machine starting from [state], returning lazily next states
     *
     * It takes also care of not re-executing already executed ones according to [State.hasBehaved],
     * and unwraps those wrapped as [AlreadyExecutedState]
     */
    fun execute(state: State): Sequence<State> = when {
        state.hasBehaved -> emptySequence()

        // drop(1) is to exclude provided [state] from next states' sequence
        else -> internalExecute(state).drop(1).map { it.unwrapIfNeeded() }
    }

    /**
     * As [execute] and wraps lazily executed states into [AlreadyExecutedState], to signal that they should not execute again
     *
     * This method is useful when implementing internal computation and
     * external execution should just skip those states execution, because already internally evaluated.
     */
    internal fun executeWrapping(state: State): Sequence<AlreadyExecutedState> =
        execute(state).map { it.asAlreadyExecuted() }

    /**
     * Internal function to execute the state behaviour lazily
     *
     * It takes also care of not re-executing already executed ones according to [State.hasBehaved]
     */
    private fun internalExecute(state: State): Sequence<State> = when {
        state.hasBehaved -> sequenceOf(state)

        else -> sequence {
            yield(state)

            state.behave().forEach { yieldAll(internalExecute(it)) }
        }
    }

    /** Utility function refactoring logic to unwrap already executed state */
    internal fun State.unwrapIfNeeded(): State = when (this) {
        is AlreadyExecutedState -> this.wrappedState
        else -> this
    }
}
