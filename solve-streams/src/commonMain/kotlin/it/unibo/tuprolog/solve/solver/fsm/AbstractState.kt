package it.unibo.tuprolog.solve.solver.fsm

import it.unibo.tuprolog.solve.Solve

/**
 * Base class for all States refactoring common behaviour
 *
 * @author Enrico
 */
abstract class AbstractState(override val solve: Solve) : State {

    override val hasBehaved = false
}
