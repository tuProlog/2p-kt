package it.unibo.tuprolog.solve.solver.fsm.state

import it.unibo.tuprolog.solve.Solve

/**
 * Represents a State of Prolog solver state-machine
 *
 * @author Enrico
 */
interface State {

    /** The [Solve.Request] or [Solve.Response] that this state carries with it*/
    val solve: Solve

    /** Makes the state behave and lazily returns next states */
    fun behave(): Sequence<State>

    /** A flag signaling if this [State.behave] has been called */
    val hasBehaved: Boolean
}
