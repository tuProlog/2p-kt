package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.isWellFormed
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import kotlinx.coroutines.CoroutineScope

/**
 * The state responsible of initializing the state machine with the goal that has to be executed
 *
 * @author Enrico
 */
internal class StateInit(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val initializedSolveRequest = solveRequest.copy(context = initializationWork(solveRequest.context))
        val currentGoal = initializedSolveRequest.query

        when {
            // current goal is already demonstrated
            with(initializedSolveRequest.context) { solverStrategies.successCheckStrategy(currentGoal, this) } ->
                yield(StateEnd.True(initializedSolveRequest, executionStrategy))

            else -> when {

                // a primitive or well-formed goal
                isWellFormed(currentGoal) ->
                    prepareForExecution(currentGoal).also { preparedGoal ->
                        yield(StateGoalEvaluation(
                                initializedSolveRequest.copy(
                                        signature = preparedGoal.extractSignature(),
                                        arguments = preparedGoal.argsList
                                ),
                                executionStrategy
                        ))
                    }

                // goal non well-formed
                else ->
                    yield(StateEnd.False(initializedSolveRequest, executionStrategy))
            }
        }
    }

    /**
     * Any state machine initialization should be done here
     *
     * It creates a new context adding given one as it's parent and resetting isChoicePointChild flag for the new context
     */
    private fun initializationWork(context: ExecutionContext): ExecutionContext =
            context.copy(
                    clauseScopedParents = sequence { yield(context); yieldAll(context.clauseScopedParents) },
                    isChoicePointChild = false
            )
}
