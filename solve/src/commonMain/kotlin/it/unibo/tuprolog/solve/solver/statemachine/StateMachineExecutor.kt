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
            internalExecutor(state).drop(1)

    /** Internal function to execute the state behaviour lazily */
    private fun internalExecutor(state: State): Sequence<State> = sequence {
        yield(sequenceOf(state))
        state.behave().forEach { yield(internalExecutor(it)) }
    }.flatten()
}
