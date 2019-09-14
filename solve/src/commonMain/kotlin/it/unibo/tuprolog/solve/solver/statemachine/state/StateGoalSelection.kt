package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.isWellFormed
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import kotlinx.coroutines.CoroutineScope

/**
 * The state responsible of making the choice of which goal will be solved next
 *
 * @author Enrico
 */
internal class StateGoalSelection( // TODO: 14/09/2019 collapse stateInit and StateGoalSelection
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoal = solveRequest.query

        when {
            // current goal is already demonstrated
            currentGoal != null && with(solveRequest.context) {
                solverStrategies.successCheckStrategy(currentGoal, this)
            } -> yield(StateEnd.True(solveRequest, executionStrategy))

            else -> when {
                // vararg primitive
                currentGoal == null ->
                    yield(StateGoalEvaluation(solveRequest, executionStrategy))

                // a primitive or well-formed goal
                isWellFormed(currentGoal) ->
                    prepareForExecution(currentGoal).also { preparedGoal ->
                        yield(StateGoalEvaluation(
                                solveRequest.copy(
                                        signature = preparedGoal.extractSignature(),
                                        arguments = preparedGoal.argsList
                                ),
                                executionStrategy
                        ))
                    }

                // goal non well-formed
                else ->
                    yield(StateEnd.False(solveRequest, executionStrategy))
            }
        }
    }
}
