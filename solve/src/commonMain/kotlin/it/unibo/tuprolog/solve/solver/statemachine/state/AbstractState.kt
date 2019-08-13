package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext
import kotlinx.coroutines.CoroutineScope

/**
 * Base class for all States refactoring common behaviour
 *
 * @author Enrico
 */
abstract class AbstractState(
        override val context: ExecutionContext,
        /** The execution scope where state behavior should execute */
        protected open val executionScope: CoroutineScope
) : State
