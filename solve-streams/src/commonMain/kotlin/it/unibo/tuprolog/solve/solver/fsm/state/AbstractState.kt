package it.unibo.tuprolog.solve.solver.fsm.state

import it.unibo.tuprolog.solve.Solve
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Base class for all States refactoring common behaviour
 *
 * @author Enrico
 */
abstract class AbstractState(
        override val solve: Solve,
        /** The execution scope where state behavior should execute computationally */
        protected open val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : State {

    override val hasBehaved = false
}
