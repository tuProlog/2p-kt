package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * Initial state that should Initialize the state-machine
 *
 * @author Enrico
 */
internal class StateInit(
        override val solveRequest: Solve.Request,
        override val executionScope: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionScope, solverStrategies) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val initializedContext = initializationWork(solveRequest.context)

        yield(
                StateGoalSelection(
                        solveRequest.copy(context = initializedContext),
                        executionScope,
                        solverStrategies
                )
        )
    }

    /** Any state machine initialization should be done here */
    private fun initializationWork(context: ExecutionContext): ExecutionContext {
        // TODO initialization?
        return context
    }
}
