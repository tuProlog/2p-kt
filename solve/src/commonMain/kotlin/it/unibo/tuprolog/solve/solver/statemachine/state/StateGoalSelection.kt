package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext

/**
 * The state responsible of making the choice of which goal will be solved next
 *
 * @author Enrico
 */
internal class StateGoalSelection(override val context: ExecutionContext) : State {

    override fun behave(): State {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}