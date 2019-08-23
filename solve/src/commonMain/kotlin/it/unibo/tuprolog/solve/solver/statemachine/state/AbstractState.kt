package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import kotlinx.coroutines.CoroutineScope

/**
 * Base class for all States refactoring common behaviour
 *
 * @author Enrico
 */
abstract class AbstractState(
        override val solveRequest: Solve.Request,
        /** The execution scope where state behavior should execute */
        protected open val executionStrategy: CoroutineScope
) : State {
    override val hasBehaved = false
}
