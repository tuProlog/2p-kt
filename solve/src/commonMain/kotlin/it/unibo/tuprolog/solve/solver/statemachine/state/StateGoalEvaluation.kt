package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of solving a selected Goal
 *
 * @author Enrico
 */
internal class StateGoalEvaluation(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionStrategy, solverStrategies) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val primitive = with(solveRequest) { context.libraries.primitives[signature] }

        primitive?.also {
            val responses = primitive(solveRequest)

            responses.forEach {
                when (it.solution) {
                    is Solution.Yes ->
                        yield(StateEnd.True(
                                with(solveRequest) {
                                    copy(context = with(context) {
                                        copy(actualSubstitution = Substitution.of(actualSubstitution, it.solution.substitution))
                                    })
                                },
                                executionStrategy,
                                solverStrategies
                        ))

                    is Solution.No ->
                        yield(StateEnd.False(solveRequest, executionStrategy, solverStrategies))
                }
            }

        } ?: yield(StateRuleSelection(solveRequest, executionStrategy, solverStrategies))
    }
}
