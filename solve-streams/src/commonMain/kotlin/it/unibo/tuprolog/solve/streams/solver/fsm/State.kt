package it.unibo.tuprolog.solve.streams.solver.fsm

import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext

/**
 * Represents a State of Prolog solver state-machine
 *
 * @author Enrico
 */
internal interface State {

    /** The [Solve.Request] or [Solve.Response] that this state carries with it */
    val solve: Solve

    /** Makes the state behave and lazily returns next states */
    fun behave(): Sequence<State>

    /** A flag signaling if this [State.behave] has been called */
    val hasBehaved: Boolean

    /** The state machine execution context in this state */
    val context: StreamsExecutionContext
}
