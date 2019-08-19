package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.solve.solver.statemachine.state.AlreadyExecutedState
import it.unibo.tuprolog.solve.solver.statemachine.state.State

/**
 * A class that makes the state machine execute its behaviour
 *
 * @author Enrico
 */
internal object StateMachineExecutor {

    /** Internal method that executes the state-machine, taking care of not re-executing states marked as already executed, and unwrapping these */
    internal fun execute(state: State): Sequence<State> =
            generateSequence(
                    if (state.hasBehaved)
                        sequenceOf((state as? AlreadyExecutedState)?.wrappedState ?: state)
                    else
                        state.behave().takeIf { it.any() }
            ) { nextStates ->
                nextFunction(nextStates, this::execute)
            }.flatten()

    /** Internal method that executes the state-machine that executes all states, even those marked as already executed */
    internal fun executeForced(state: State): Sequence<State> = // TODO maybe this version is useless... and can be removed afterwards
            generateSequence(
                    state.behave().takeIf { it.any() }
            ) { nextStates ->
                nextFunction(nextStates, this::executeForced)
            }.flatten()

    /** Utility function refactoring sequence "next function" logic; it unwraps [AlreadyExecutedState] if any */
    private fun nextFunction(nextStates: Sequence<State>, stateExecutionFunction: (State) -> Sequence<State>): Sequence<State>? =
            sequence { nextStates.forEach { yield(stateExecutionFunction(it)) } }
                    // TODO cut execution should be catch here and make subsequent evaluation to be cancelled
                    .flatten()
                    .takeIf { it.any() }
}
