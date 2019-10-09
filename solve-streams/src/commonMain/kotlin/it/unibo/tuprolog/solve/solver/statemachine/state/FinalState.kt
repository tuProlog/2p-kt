package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve

/**
 * Represents a Final [State] of the Prolog solver state-machine
 *
 * @author Enrico
 */
interface FinalState : State {

    /** The [Solve.Response] characterizing this Final State */
    override val solve: Solve.Response

}
