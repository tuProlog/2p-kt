package it.unibo.tuprolog.solve.streams.solver.fsm

import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Base class for all States refactoring common behaviour
 *
 * @author Enrico
 */
internal abstract class AbstractState(
    override val solve: Solve,
) : State {
    override val hasBehaved = false
}
