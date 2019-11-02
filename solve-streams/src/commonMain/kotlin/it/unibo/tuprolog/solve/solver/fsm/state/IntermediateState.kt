package it.unibo.tuprolog.solve.solver.fsm.state

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.Solve.Response

/**
 * Represents an Intermediate [State] of the Prolog solver state-machine
 *
 * @author Enrico
 */
interface IntermediateState : State {

    /** The [Solve.Request] that guides the State behaviour towards [Response]s */
    override val solve: Solve.Request<ExecutionContext>

}
