package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of selecting a rule to be solved to demonstrate a goal
 *
 * @author Enrico
 */
internal class StateRuleSelection(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionStrategy, solverStrategies) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoal = with(solveRequest) { signature.withArgs(arguments)!! }
        val matchingClauses = solveRequest.context.libraries.theory[currentGoal]

        when {
            matchingClauses.none() -> yield(StateEnd.False(solveRequest, executionStrategy, solverStrategies))

            else -> clausesOrdered(matchingClauses, solveRequest.context, solverStrategies::clauseChoiceStrategy).forEach {
                yield(StateGoalSelection(
                        solveRequest.copy(),
                        executionStrategy,
                        solverStrategies
                ))
                TODO("copy information to new solveRequest")
            }
        }
    }

    /** Computes the ordered selection of clauses, according to provided selection strategy */
    private fun <C : Clause> clausesOrdered(
            clauses: Sequence<C>,
            context: ExecutionContext,
            selectionStrategy: (Sequence<C>, ExecutionContext) -> C
    ): Sequence<C> = sequence {
        mutableListOf<C>()
                .also { selected ->
                    repeat(clauses.count()) { yield(selectionStrategy(clauses - selected, context)) }
                }
    }

}
