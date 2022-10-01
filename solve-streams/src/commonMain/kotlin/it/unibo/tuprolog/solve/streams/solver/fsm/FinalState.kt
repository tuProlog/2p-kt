package it.unibo.tuprolog.solve.streams.solver.fsm

import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Represents a Final [State] of the Prolog solver state-machine
 *
 * @author Enrico
 */
internal interface FinalState : State {

    /** The [Solve.Response] characterizing this Final State */
    override val solve: Solve.Response
}
