package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve

/**
 * Represents a State of Prolog solver state-machine
 *
 * @author Enrico
 */
interface State {

    /** The context in which the State should behave */
    val solveRequest: Solve.Request

    /** Makes the state behave and lazily returns next states */
    fun behave(): Sequence<State>

}
