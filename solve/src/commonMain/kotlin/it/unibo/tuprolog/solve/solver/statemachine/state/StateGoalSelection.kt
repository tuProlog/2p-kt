package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.isWellFormed
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import kotlinx.coroutines.CoroutineScope

/**
 * The state responsible of making the choice of which goal will be solved next
 *
 * @author Enrico
 */
internal class StateGoalSelection(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoal = with(solveRequest) { signature.withArgs(arguments) }

        when {
            // current goal is already demonstrated
            currentGoal != null && with(solveRequest.context) {
                solverStrategies.successCheckStrategy(currentGoal, this)
            } -> yield(StateEnd.True(solveRequest, executionStrategy))

            else -> when {
                // vararg primitive
                currentGoal == null ->
                    yield(StateGoalEvaluation(solveRequest, executionStrategy))

                isWellFormed(currentGoal) ->
                    prepareForExecution(currentGoal).also { preparedGoal ->
                        yield(StateGoalEvaluation(
                                solveRequest.copy(
                                        signature = Signature.fromIndicator(preparedGoal.indicator)!!,
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
