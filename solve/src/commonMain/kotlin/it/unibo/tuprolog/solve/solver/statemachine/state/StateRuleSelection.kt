package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverStrategies
import it.unibo.tuprolog.solve.solver.SolverUtils
import it.unibo.tuprolog.solve.solver.SolverUtils.orderedWithStrategy
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.statemachine.currentTime
import it.unibo.tuprolog.unify.Unification.Companion.mguWith
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
        val matchingRules = solveRequest.context.libraries.theory[currentGoal.freshCopy()]
                .map { it.freshCopy() }

        when {
            matchingRules.none() -> yield(StateEnd.False(solveRequest, executionStrategy, solverStrategies))

            else -> orderedWithStrategy(matchingRules, solveRequest.context, solverStrategies::clauseChoiceStrategy).forEach { clause ->
                val unifyingSubstitution = currentGoal mguWith clause.head

                // rules reaching this state are considered to be implicitly wellFormed
                val wellFormedRuleBody = clause.body.apply(unifyingSubstitution) as Struct

                val newSolveRequest = SolverUtils.createNewGoalSolveRequest(
                        solveRequest, wellFormedRuleBody, currentTime(), unifyingSubstitution)

                StateMachineExecutor
                        .executeWrapping(StateInit(newSolveRequest, executionStrategy, solverStrategies))
                        .forEach {
                            yield(it)

                            // find in sub-goal state sequence, the state responding to actual solveRequest
                            if (with(it.wrappedState) { this is FinalState && solveRequest.equalSignatureAndArgs(newSolveRequest) }) {
                                yield(when (it.wrappedState) {
                                    is SuccessFinalState ->
                                        StateEnd.True(
                                                solveRequest.copy(context = with(solveRequest.context) {
                                                    copy(actualSubstitution = Substitution.of(
                                                            actualSubstitution,
                                                            it.wrappedState.solveRequest.context.actualSubstitution
                                                    ))
                                                }),
                                                executionStrategy,
                                                solverStrategies
                                        )
                                    else ->
                                        StateEnd.False(solveRequest, executionStrategy, solverStrategies)
                                })
                            }
                        }
            }
        }
    }
}
