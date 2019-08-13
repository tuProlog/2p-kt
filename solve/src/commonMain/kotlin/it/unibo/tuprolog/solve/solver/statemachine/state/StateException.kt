package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * State that's responsible of managing exceptions that can be thrown during execution
 *
 * @author Enrico
 */
internal class StateException(override val context: ExecutionContext) : State {

    override fun behave(): State {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
