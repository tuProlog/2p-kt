package it.unibo.tuprolog.solve.solver.fsm.state

import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.SolverUtils.isWellFormed
import it.unibo.tuprolog.solve.solver.SolverUtils.prepareForExecution
import it.unibo.tuprolog.solve.solver.fsm.stateEndFalse
import it.unibo.tuprolog.solve.solver.fsm.stateEndTrue
import kotlinx.coroutines.CoroutineScope

/**
 * The state responsible of initializing the state machine with the goal that has to be executed
 *
 * @author Enrico
 */
internal class StateInit(
        override val solve: Solve.Request<ExecutionContextImpl>,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solve, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val initializedRequest = solve.initializeForSideEffects()
        val currentGoal = initializedRequest.query

        when {
            // current goal is already demonstrated
            with(initializedRequest.context) { solverStrategies.successCheckStrategy(currentGoal, this) } ->
                yield(with(initializedRequest.context) { stateEndTrue(substitution, sideEffectManager = sideEffectManager) })

            else -> when {

                // a primitive or well-formed goal
                isWellFormed(currentGoal) ->
                    prepareForExecution(currentGoal).also { preparedGoal ->
                        yield(StateGoalEvaluation(
                                initializedRequest.copy(
                                        signature = preparedGoal.extractSignature(),
                                        arguments = preparedGoal.argsList
                                ),
                                executionStrategy
                        ))
                    }

                // goal non well-formed
                else -> yield(stateEndFalse(sideEffectManager = initializedRequest.context.sideEffectManager))
            }
        }
    }

    /** Initializes the SideEffectsManager if is correct instance, does nothing otherwise */
    private fun Solve.Request<ExecutionContextImpl>.initializeForSideEffects() =
            copy(context = with(context) {
                copy(
                        sideEffectManager = (sideEffectManager as? SideEffectManagerImpl)
                                ?.run { sideEffectManager.stateInitInitialize(this@with) }
                                ?: sideEffectManager
                )
            })
}
