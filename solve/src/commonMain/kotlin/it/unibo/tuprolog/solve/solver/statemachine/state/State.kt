package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.Solve.Response

/**
 * Represents a State of Prolog solver state-machine
 *
 * @author Enrico
 */
interface State {

    /** The request that guides the State behaviour towards [Response]s */
    val solveRequest: Solve.Request<ExecutionContextImpl>

    /** Makes the state behave and lazily returns next states */
    fun behave(): Sequence<State>

    /** A flag signaling if this [State.behave] has been called */
    val hasBehaved: Boolean
}
