package it.unibo.tuprolog.solve.streams.solver.fsm

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext

/**
 * Represents an Intermediate [State] of the Prolog solver state-machine
 *
 * @author Enrico
 */
internal interface IntermediateState : State {
    /** The [Solve.Request] that drives the State behaviour towards [Response]s */
    override val solve: Solve.Request<ExecutionContext>

    override val context: StreamsExecutionContext
        get() = solve.context as StreamsExecutionContext
}
