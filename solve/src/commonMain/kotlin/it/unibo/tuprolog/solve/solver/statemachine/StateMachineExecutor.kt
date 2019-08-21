package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.solve.solver.statemachine.state.State

/**
 * A class that makes the state machine execute its behaviour
 *
 * @author Enrico
 */
internal object StateMachineExecutor {

    /** Internal method that executes the state-machine, returning lazily next states */
    internal fun execute(state: State): Sequence<State> =
            // drop(1) is to exclude provided state from next states' sequence
            internalExecute(state).drop(1)

    /** Internal function to execute the state behaviour lazily */
    private fun internalExecute(state: State): Sequence<State> = sequence {
        yield(sequenceOf(state))
        // TODO cut execution should be catch here and make subsequent evaluation to be cancelled
        state.behave().forEach { yield(internalExecute(it)) }
    }.flatten()
}
