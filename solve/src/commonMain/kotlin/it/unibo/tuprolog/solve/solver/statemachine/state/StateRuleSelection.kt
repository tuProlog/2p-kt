package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.solve.Solve
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
        val currentGoal = with(solveRequest) { signature.withArgs(arguments)!! }
        val matchingRules = solveRequest.context.libraries.theory[currentGoal.freshCopy()]
        val isChoicePoint = moreThanOne(matchingRules)

        when {
            matchingRules.none() -> yield(StateEnd.False(solveRequest, executionStrategy))

            else -> with(solveRequest.context) {
                orderedWithStrategy(matchingRules, this, this.solverStrategies::clauseChoiceStrategy)
            }
                    .map { it.freshCopy() }
                    .map { refreshedRule ->
                        val unifyingSubstitution = currentGoal mguWith refreshedRule.head

                        // rules reaching this state are considered to be implicitly wellFormed
                        val wellFormedRuleBody = refreshedRule.body.apply(unifyingSubstitution) as Struct

                        solveRequest.newSolveRequest(wellFormedRuleBody, unifyingSubstitution, isChoicePointChild = isChoicePoint)
                                // clear parents for this new "cut scope"
                                .let { it.copy(context = it.context.copy(parents = sequenceOf(solveRequest.context))) }
                    }.forEach { subSolveRequest ->
                        val subInitialState = StateInit(subSolveRequest, executionStrategy)
                                .also { yield(it.alreadyExecuted()) }

                        var cutNextSiblings = false

                        subStateExecute(subInitialState).forEach {
                            yield(it)

                            if (solveRequest.context in it.solveRequest.context.toCutContextsParent)
                                cutNextSiblings = true

                            // find in sub-goal state sequence, the state responding to current solveRequest
                            if (with(it.wrappedState) { this is FinalState && solveRequest.equalSignatureAndArgs(subSolveRequest) }) {
                                yield(
                                        when (it.wrappedState) {
                                            is SuccessFinalState ->
                                                StateEnd.True(
                                                        solveRequest.importingContextFrom(it.wrappedState.solveRequest),
                                                        executionStrategy
                                                )
                                            else ->
                                                StateEnd.False(
                                                        solveRequest.importingContextFrom(it.wrappedState.solveRequest),
                                                        executionStrategy
                                                )
                                        }
                                )
                            }
                        }
                        if (cutNextSiblings) return@sequence // cut here matching rules trial
                    }
        }
    }

    // TODO this should be moved to [SolverUtils], and tested
    /** Utility method to copy receiver [Solve.Request] importing [subSolveRequest] context */
    private fun Solve.Request.importingContextFrom(subSolveRequest: Solve.Request) = this
            .copy(context = with(this.context) {
                copy(
                        currentSubstitution = Substitution.of(
                                subSolveRequest.context.currentSubstitution,
                                currentSubstitution.mapValues { (_, replacement) ->
                                    replacement.apply(subSolveRequest.context.currentSubstitution)
                                }.asUnifier()
                        ),
                        parents = sequence { yield(this@with); yieldAll(parents) }, // refactor this into utility method insertAtBeginning for Seqeunces
                        toCutContextsParent = subSolveRequest.context.toCutContextsParent
                )
            })
}
