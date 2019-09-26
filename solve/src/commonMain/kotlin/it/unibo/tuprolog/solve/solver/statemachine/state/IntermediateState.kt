package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.Solve.Response
import it.unibo.tuprolog.solve.solver.DeclarativeImplExecutionContext

/**
 * Represents an Intermediate [State] of the Prolog solver state-machine
 *
 * @author Enrico
 */
interface IntermediateState : State {

    /** The [Solve.Request] that guides the State behaviour towards [Response]s */
    override val solve: Solve.Request<DeclarativeImplExecutionContext>

}
