package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.SideEffectManager
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
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
            matchingRules.none() -> yield(stateEndFalse(sideEffectManager = solve.context.sideEffectManager))

            else -> with(solve.context) { orderedWithStrategy(matchingRules, this, solverStrategies::clauseChoiceStrategy) }
                    .map { it.freshCopy() }
                    .map { refreshedRule ->
                        val unifyingSubstitution = currentGoal mguWith refreshedRule.head

                        val wellFormedRuleBody = refreshedRule.body.apply(unifyingSubstitution) as Struct

                        solve.newSolveRequest(wellFormedRuleBody, unifyingSubstitution, isChoicePointChild = isChoicePoint)

                    }.forEach { subSolveRequest ->
                        val subInitialState = StateInit(prepareSubSolveRequest(subSolveRequest), executionStrategy)
                                .also { yield(it.asAlreadyExecuted()) }

                        var cutNextSiblings = false

                        // execute internally the sub-request in a sub-state-machine, to see what it will respond
                        subStateExecute(subInitialState).forEach {
                            yield(it)

                            // find in sub-goal state sequence, the final state responding to current solveRequest
                            if (with(it.wrappedState) { this is FinalState && solve.solution.query == subSolveRequest.query }) {
                                val subEndState = it.wrappedState as StateEnd

                                if ((subEndState.solve.sideEffectManager as? SideEffectManagerImpl)?.run { shouldCutExecuteInRuleSelection() } == true)
                                    cutNextSiblings = true

                                yield(stateEnd(with(subEndState.solve) {
                                    copy(sideEffectManager = extendParentScopeIfPossible(sideEffectManager, solve.context.sideEffectManager))
                                }))

                                if (it.wrappedState is StateEnd.Halt) return@sequence // if halt reached, overall computation should stop
                            }
                        }
                        if (cutNextSiblings) return@sequence // cut here other matching rules trial
                    }
        }
    }

    /** Prepares provided solveRequest "side effects manager" to enter this "rule sub-scope" */
    private fun prepareSubSolveRequest(solveRequest: Solve.Request<ExecutionContextImpl>) =
            solveRequest.copy(context = with(solveRequest.context) { copy(sideEffectManager = sideEffectManager.enterRuleSubScope()) })

    /** Extends parent clauses scope to include upper-scope ones, using side effect manager method, if correct instances are provided  */
    private fun extendParentScopeIfPossible(responseManager: SideEffectManager?, requestManager: SideEffectManager): SideEffectManager? =
            (responseManager as? SideEffectManagerImpl)
                    ?.run { (requestManager as? SideEffectManagerImpl)?.let { extendParentScopeWith(requestManager) } }
                    ?: responseManager

}
