package it.unibo.tuprolog.solve.streams.solver.fsm.impl

import it.unibo.tuprolog.solve.extractSignature
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.fsm.State
import it.unibo.tuprolog.solve.streams.solver.isWellFormed
import it.unibo.tuprolog.solve.streams.solver.prepareForExecutionAsGoal

/**
 * The state responsible of initializing the state machine with the goal that has to be executed
 *
 * @author Enrico
 */
internal class StateInit(
    override val solve: Solve.Request<StreamsExecutionContext>
) : AbstractTimedState(solve) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val initializedSideEffectsManager = with(solve.context) { sideEffectManager.stateInitInitialize(this) }
        val currentGoal = solve.query

        when {
            with(solve.context) { solverStrategies.successCheckStrategy(currentGoal, this) } ->
                // current goal is already demonstrated
                yield(
                    ifTimeIsNotOver(
                        stateEndTrue(
                            solve.context.substitution,
                            sideEffectManager = initializedSideEffectsManager
                        )
                    )
                )

            else -> when {
                currentGoal.isWellFormed() -> currentGoal.prepareForExecutionAsGoal().also { preparedGoal ->
                    // a primitive call or well-formed goal
                    yield(
                        StateGoalEvaluation(
                            solve.copy(
                                signature = preparedGoal.extractSignature(),
                                arguments = preparedGoal.argsList,
                                context = with(solve.context) { copy(sideEffectManager = initializedSideEffectsManager) }
                            )
                        )
                    )
                }

                // goal non well-formed
                else -> yield(stateEndFalse(sideEffectManager = initializedSideEffectsManager))
            }
        }
    }
}
