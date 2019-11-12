package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.isWellFormed
import it.unibo.tuprolog.solve.solver.prepareForExecutionAsGoal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * The state responsible of initializing the state machine with the goal that has to be executed
 *
 * @author Enrico
 */
internal class StateInit(
        override val solve: Solve.Request<ExecutionContextImpl>,
        override val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : AbstractTimedState(solve, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val initializedSideEffectsManager = with(solve.context) { sideEffectManager.stateInitInitialize(this) }
        val currentGoal = solve.query

        when {
            with(solve.context) { solverStrategies.successCheckStrategy(currentGoal, this) } ->
                // current goal is already demonstrated
                yield(stateEndTrue(solve.context.substitution, sideEffectManager = initializedSideEffectsManager))

            else -> when {
                currentGoal.isWellFormed() -> currentGoal.prepareForExecutionAsGoal().also { preparedGoal ->
                    // a primitive call or well-formed goal
                    yield(StateGoalEvaluation(
                            solve.copy(
                                    signature = preparedGoal.extractSignature(),
                                    arguments = preparedGoal.argsList,
                                    context = with(solve.context) { copy(sideEffectManager = initializedSideEffectsManager) }
                            ),
                            executionStrategy
                    ))
                }

                // goal non well-formed
                else -> yield(stateEndFalse(sideEffectManager = initializedSideEffectsManager))
            }
        }
    }
}
