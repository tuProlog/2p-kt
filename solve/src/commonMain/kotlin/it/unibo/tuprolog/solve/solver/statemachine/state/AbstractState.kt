package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * Base class for all States refactoring common behaviour
 *
 * @author Enrico
 */
abstract class AbstractState(
        override val solveRequest: Solve.Request,
        /** The execution scope where state behavior should execute */
        protected open val executionScope: CoroutineScope,
        /** The [SolverStrategies] to be applied during solution process */
        protected open val solverStrategies: SolverStrategies
) : State
