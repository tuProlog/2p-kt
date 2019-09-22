package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.SolverUtils.importingContextFrom
import it.unibo.tuprolog.solve.solver.SolverUtils.moreThanOne
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.orderedWithStrategy
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
        val currentGoal = solveRequest.query!!
        val matchingRules = solveRequest.context.libraries.theory[currentGoal.freshCopy()] // TODO: 22/09/2019 check for matching rules even in staticKB and dynamicKB
        val isChoicePoint = moreThanOne(matchingRules)

        when {
            matchingRules.none() -> yield(StateEnd.False(solveRequest, executionStrategy))

            else -> with(solveRequest.context) { orderedWithStrategy(matchingRules, this, solverStrategies::clauseChoiceStrategy) }
                    .map { it.freshCopy() }
                    .map { refreshedRule ->
                        val unifyingSubstitution = currentGoal mguWith refreshedRule.head

                        val wellFormedRuleBody = refreshedRule.body.apply(unifyingSubstitution) as Struct

                        solveRequest.newSolveRequest(wellFormedRuleBody, unifyingSubstitution, isChoicePointChild = isChoicePoint)

                    }.forEach { subSolveRequest ->
                        val subInitialState = StateInit(
                                // clear older parents entering this new "sub-rule scope"
                                subSolveRequest.copy(context = subSolveRequest.context.copy(clauseScopedParents = sequenceOf(solveRequest.context))),
                                executionStrategy
                        ).also { yield(it.asAlreadyExecuted()) }

                        var cutNextSiblings = false

                        // execute internally the sub-request in a sub-state-machine, to see what it will respond
                        subStateExecute(subInitialState).forEach {
                            yield(it)

                            if (solveRequest.context in it.solveRequest.context.toCutContextsParent
                                    || it.solveRequest.context.logicalParentRequests.any { parentRequest -> parentRequest.context == it.solveRequest.context.throwRelatedToCutContextsParent })
                                cutNextSiblings = true

                            // find in sub-goal state sequence, the final state responding to current solveRequest
                            if (with(it.wrappedState) { this is FinalState && solveRequest.equalSignatureAndArgs(subSolveRequest) }) {
                                val endState = it.wrappedState as StateEnd

                                yield(endState.makeCopy(solveRequest.importingContextFrom(it.wrappedState.solveRequest)))

                                // if halt reached, overall computation should stop (duplicated in StateGoalEvaluation.. refactor the logic)
                                if (it.wrappedState is StateEnd.Halt) return@sequence
                            }
                        }
                        if (cutNextSiblings) return@sequence // cut here other matching rules trial
                    }
        }
    }
}
