package it.unibo.tuprolog.solve.streams.solver.fsm.impl

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.forEachWithLookahead
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.extendParentScopeWith
import it.unibo.tuprolog.solve.streams.solver.fsm.AlreadyExecutedState
import it.unibo.tuprolog.solve.streams.solver.fsm.FinalState
import it.unibo.tuprolog.solve.streams.solver.fsm.State
import it.unibo.tuprolog.solve.streams.solver.fsm.StateMachineExecutor
import it.unibo.tuprolog.solve.streams.solver.fsm.asAlreadyExecuted
import it.unibo.tuprolog.solve.streams.solver.moreThanOne
import it.unibo.tuprolog.solve.streams.solver.newSolveRequest
import it.unibo.tuprolog.solve.streams.solver.orderWithStrategy
import it.unibo.tuprolog.solve.streams.solver.shouldCutExecuteInRuleSelection

/**
 * State responsible of selecting a rule to be solved to demonstrate a goal
 *
 * @author Enrico
 */
internal class StateRuleSelection(
    override val solve: Solve.Request<StreamsExecutionContext>
) : AbstractTimedState(solve) {

    /** The execute function to be used when a [State] needs, internally, to execute sub-[State]s behaviour */
    private val subStateExecute: (State) -> Sequence<AlreadyExecutedState> = StateMachineExecutor::executeWrapping

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoal = solve.query
        val matchingRules = solve.context.retrieveRulesMatching(currentGoal)
        val isChoicePoint = moreThanOne(matchingRules)

        when {
            matchingRules.none() -> yield(stateEndFalse())

            else ->
                with(solve.context) {
                    matchingRules.orderWithStrategy(this, solverStrategies::clauseChoiceStrategy)
                }.map { it.prepareForExecution().freshCopy() as Rule }
                    .forEachWithLookahead { refreshedRule, hasAlternatives ->
                        val unifyingSubstitution = context.unificator.mgu(currentGoal, refreshedRule.head)

                        val wellFormedRuleBody = refreshedRule.body.apply(unifyingSubstitution) as Struct

                        val subSolveRequest =
                            solve.newSolveRequest(
                                wellFormedRuleBody,
                                unifyingSubstitution,
                                isChoicePointChild = isChoicePoint,
                                requestIssuingInstant = currentTimeInstant()
                            )

                        val subInitialState = StateInit(subSolveRequest.initializeForSubRuleScope())
                            .also { yield(it.asAlreadyExecuted()) }

                        var cutNextSiblings = false

                        // execute internally the sub-request in a sub-state-machine, to see what it will respond
                        subStateExecute(subInitialState).forEach {
                            val subState = it.wrappedState

                            // find in sub-goal state sequence, the final state responding to current solveRequest
                            if (subState is FinalState && subState.solve.solution.query == subSolveRequest.query) {
                                if (subState.solve.sideEffectManager.shouldCutExecuteInRuleSelection()) {
                                    cutNextSiblings = true
                                }

                                // yield only non-false states or false states when there are no open alternatives (because no more or cut)
                                if (subState !is StateEnd.False || !hasAlternatives || cutNextSiblings) {
                                    val extendedScopeSideEffectManager = subState.solve.sideEffectManager
                                        .extendParentScopeWith(solve.context.sideEffectManager)

                                    yield(
                                        stateEnd(
                                            subState.solve.copy(
                                                solution = subState.solve.solution.removeSubstitutionFor(refreshedRule.variables),
                                                sideEffectManager = extendedScopeSideEffectManager
                                            )
                                        )
                                    )
                                }

                                if (subState is StateEnd.Halt) return@sequence // if halt reached, overall computation should stop
                            } else yield(it) // return wrapped subState as is, only if not interested in it
                        }
                        if (cutNextSiblings) return@sequence // cut here other matching rules trial
                    }
        }
    }

    private companion object {

        /**
         * Retrieves from receiver [ExecutionContext] those rules whose head matches [currentGoal]
         *
         * 1) It searches for matches inside libraries, if nothing found
         * 2) it looks inside both staticKb and dynamicKb
         */
        private fun ExecutionContext.retrieveRulesMatching(currentGoal: Struct): Sequence<Rule> =
            currentGoal.freshCopy().let { refreshedGoal ->
                libraries.asTheory(unificator)[refreshedGoal].takeIf { it.any() }
                    ?: sequenceOf(staticKb, dynamicKb).flatMap { it[refreshedGoal] }
            }

        /** Prepares provided solveRequest "side effects manager" to enter this "rule body sub-scope" */
        private fun Solve.Request<StreamsExecutionContext>.initializeForSubRuleScope() =
            copy(context = with(context) { copy(sideEffectManager = sideEffectManager.enterRuleSubScope()) })

        /**
         * Utility function to eliminate from solution substitution non meaningful variables
         * for the "upper scope" query, (i.e. variables introduced only for solving the "current" query)
         */
        private fun Solution.removeSubstitutionFor(unusedVariables: Sequence<Var>) = when (this) {
            is Solution.Yes -> copy(substitution = substitution - unusedVariables.asIterable())
            else -> this
        }
    }
}
