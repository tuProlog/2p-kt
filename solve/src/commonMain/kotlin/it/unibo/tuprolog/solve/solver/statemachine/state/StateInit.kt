package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * Initial state that should Initialize the state-machine
 *
 * @author Enrico
 */
internal class StateInit(override val context: ExecutionContext) : State {

    override fun behave(): State {
        val initializedContext = initializationWork(context)

        return StateGoalSelection(initializedContext)
    }

    /** Any state machine initialization should be done here */
    private fun initializationWork(context: ExecutionContext): ExecutionContext {
        // TODO initialization?
        return context
    }
}
