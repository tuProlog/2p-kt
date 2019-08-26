package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.orderedWithStrategy
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.unify.Unification.Companion.mguWith
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of selecting a rule to be solved to demonstrate a goal
 *
 * @author Enrico
 */
internal class StateRuleSelection(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoal = with(solveRequest) { signature.withArgs(arguments)!! }
        val matchingRules = solveRequest.context.libraries.theory[currentGoal.freshCopy()]

        when {
            matchingRules.none() -> yield(StateEnd.False(solveRequest, executionStrategy))

            else -> with(solveRequest.context) {
                orderedWithStrategy(matchingRules, this, this.solverStrategies::clauseChoiceStrategy)
            }.forEach { clause ->
                val unifyingSubstitution = currentGoal mguWith clause.head

                // rules reaching this state are considered to be implicitly wellFormed
                val wellFormedRuleBody = clause.body.apply(unifyingSubstitution) as Struct

                val newSolveRequest = solveRequest.newSolveRequest(
                        newGoal = wellFormedRuleBody,
                        toAddSubstitutions = unifyingSubstitution
                )

                StateMachineExecutor
                        .executeWrapping(StateInit(newSolveRequest, executionStrategy))
                        .forEach {
                            yield(it)

                            // find in sub-goal state sequence, the state responding to actual solveRequest
                            if (with(it.wrappedState) { this is FinalState && solveRequest.equalSignatureAndArgs(newSolveRequest) }) {
                                yield(
                                        when (it.wrappedState) {
                                            is SuccessFinalState ->
                                                StateEnd.True(
                                                        solveRequest.importingSubstitutionFrom(it.wrappedState.solveRequest),
                                                        executionStrategy
                                                )
                                            else ->
                                                StateEnd.False(solveRequest, executionStrategy)
                                        }
                                )
                            }
                        }
            }
        }
    }

    /** Utility method to copy receiver [Solve.Request] importing [subSolveRequest] substitution */
    private fun Solve.Request.importingSubstitutionFrom(subSolveRequest: Solve.Request) = this
            .copy(context = with(solveRequest.context) {
                copy(actualSubstitution = Substitution.of(
                        actualSubstitution,
                        subSolveRequest.context.actualSubstitution
                ))
            })
}
