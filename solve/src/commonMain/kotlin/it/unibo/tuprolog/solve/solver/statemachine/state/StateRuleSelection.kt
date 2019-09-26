package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SolverUtils.moreThanOne
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.orderedWithStrategy
import it.unibo.tuprolog.solve.solver.statemachine.stateEnd
import it.unibo.tuprolog.solve.solver.statemachine.stateEndFalse
import it.unibo.tuprolog.unify.Unification.Companion.mguWith
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of selecting a rule to be solved to demonstrate a goal
 *
 * @author Enrico
 */
internal class StateRuleSelection(
        override val solve: Solve.Request<ExecutionContextImpl>,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solve, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoal = solve.query
        val matchingRules = solve.context.libraries.theory[currentGoal.freshCopy()] // TODO: 22/09/2019 check for matching rules even in staticKB and dynamicKB
        val isChoicePoint = moreThanOne(matchingRules)

        when {
            matchingRules.none() -> yield(stateEndFalse(contextImpl = solve.context))

            else -> with(solve.context) { orderedWithStrategy(matchingRules, this, solverStrategies::clauseChoiceStrategy) }
                    .map { it.freshCopy() }
                    .map { refreshedRule ->
                        val unifyingSubstitution = currentGoal mguWith refreshedRule.head

                        val wellFormedRuleBody = refreshedRule.body.apply(unifyingSubstitution) as Struct

                        solve.newSolveRequest(wellFormedRuleBody, unifyingSubstitution, isChoicePointChild = isChoicePoint)

                    }.forEach { subSolveRequest ->
                        val subInitialState = StateInit(
                                // clear older parents entering this new "sub-rule scope"
                                subSolveRequest.copy(context = subSolveRequest.context.copy(clauseScopedParents = sequenceOf(solve.context))),
                                executionStrategy
                        ).also { yield(it.asAlreadyExecuted()) }

                        var cutNextSiblings = false

                        // execute internally the sub-request in a sub-state-machine, to see what it will respond
                        subStateExecute(subInitialState).forEach {
                            yield(it)

                            // find in sub-goal state sequence, the final state responding to current solveRequest
                            if (with(it.wrappedState) { this is FinalState && solve.solution.query == subSolveRequest.query }) {
                                val subEndState = it.wrappedState as StateEnd

                                if (solve.context in (subEndState.solve.context as ExecutionContextImpl).toCutContextsParent
                                        || (subEndState.solve.context as ExecutionContextImpl).logicalParentRequests.any { parentRequest -> parentRequest.context == (subEndState.solve.context as ExecutionContextImpl).throwRelatedToCutContextsParent })
                                    cutNextSiblings = true

                                yield(stateEnd(with(subEndState.solve) {
                                    copy(context = context!!.copy(clauseScopedParents = sequence {
                                        yieldAll(context.clauseScopedParents)
                                        yieldAll(solve.context.clauseScopedParents)
                                    }))
                                }))

                                if (it.wrappedState is StateEnd.Halt) return@sequence // if halt reached, overall computation should stop
                            }
                        }
                        if (cutNextSiblings) return@sequence // cut here other matching rules trial
                    }
        }
    }
}
