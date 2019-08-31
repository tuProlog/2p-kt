package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.exception.HaltException
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of solving a selected Goal
 *
 * @author Enrico
 */
internal class StateGoalEvaluation(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val primitive = with(solveRequest) { context.libraries.primitives[signature] }

        primitive?.also {
            var responses: Sequence<Solve.Response>? = null
            try {
                responses = primitive(solveRequest)
            } catch (e: HaltException) {
                yield(StateEnd.Halt(solveRequest, executionStrategy))
            }

            responses?.forEach {
                when (it.solution) {
                    is Solution.Yes ->
                        yield(StateEnd.True(
                                solveRequest.copy(context = it.context),
                                executionStrategy
                        ))

                    is Solution.No ->
                        yield(StateEnd.False(
                                solveRequest.copy(context = it.context),
                                executionStrategy
                        ))
                }
            }

        } ?: yield(StateRuleSelection(solveRequest, executionStrategy))
    }
}
