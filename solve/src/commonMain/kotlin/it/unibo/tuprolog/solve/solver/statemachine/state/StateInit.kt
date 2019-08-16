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
        override val executionStrategy: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionStrategy, solverStrategies) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoalStruct = with(solveRequest) { signature.withArgs(arguments) }

        when {
            with(currentGoalStruct) { this != null && solverStrategies.successCheckStrategy(this) } ->
                yield(StateEnd.True(solveRequest, executionStrategy, solverStrategies))

            else ->
                yield(
                        StateGoalSelection(
                                solveRequest.copy(context = initializationWork(solveRequest.context)),
                                executionStrategy,
                                solverStrategies
                        )
                )
        }
    }

    /** Any state machine initialization should be done here */
    private fun initializationWork(context: ExecutionContext): ExecutionContext {
        // TODO initialization?
        return context
    }
}
