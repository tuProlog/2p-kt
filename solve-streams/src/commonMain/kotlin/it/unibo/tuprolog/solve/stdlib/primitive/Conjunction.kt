package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.solver.*

/**
 * Implementation of primitive handling `','/2` behaviour
 *
 * @author Enrico
 */
internal object Conjunction : PrimitiveWrapper<StreamsExecutionContext>(Tuple.FUNCTOR, 2) {

    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        sequence {
            val subGoals = with(request) {
                query.`as`<Tuple>().toSequence()
                    .orderWithStrategy(context, context.solverStrategies::predicationChoiceStrategy)
                    .toList()
            }

            solveConjunctionGoals(
                request,
                subGoals,
                request.context,
                Substitution.empty(),
                emptyList(),
                request.context.sideEffectManager,
                previousGoalsHadAlternatives = false
            )
        }

    /**
     * This utility method implements the conjunction solving procedure
     *
     * @param mainRequest The initial conjunction request with all goals, to be referred as the only logical parent
     * @param goals The goals in conjunction together, to be solved left to right
     * @param accumulatedSubstitutions This is a substitution accumulator, that maintains the diff from the [mainRequest] substitution
     * @param previousResponseSideEffectManager This is the previous response side effect manager, needed to propagate information during execution
     * @param previousGoalsHadAlternatives This signals if previous conjunction goals had unexplored alternatives
     *
     * @return The boolean value signaling if the following goals executed the cut or not
     */
    private suspend fun SequenceScope<Solve.Response>.solveConjunctionGoals(
        mainRequest: Solve.Request<StreamsExecutionContext>,
        goals: Iterable<Term>,
        toPropagateContext: ExecutionContext,
        accumulatedSubstitutions: Substitution,
        accumulatedSideEffects: List<SideEffect>,
        previousResponseSideEffectManager: SideEffectManagerImpl?,
        previousGoalsHadAlternatives: Boolean
    ): Boolean {
        val goal = goals.first().apply(accumulatedSubstitutions).prepareForExecutionAsGoal()

        val goalRequest = mainRequest.newSolveRequest(
            goal,
            accumulatedSubstitutions,
            toPropagateContext,
            baseSideEffectManager = previousResponseSideEffectManager ?: mainRequest.context.sideEffectManager
        )

        var cutExecuted = false
        StreamsSolver.solveToFinalStates(goalRequest).forEachWithLookahead { goalFinalState, currentHasAlternatives ->
            val goalResponse = goalFinalState.solve
            if (Cut.functor == goal.functor || goalResponse.sideEffectManager?.shouldExecuteThrowCut() == true)
                cutExecuted = true

            val allSideEffectsSoFar = accumulatedSideEffects + goalResponse.sideEffects

            when {
                goalResponse.sideEffectManager?.shouldExecuteThrowCut() == false &&
                        goalResponse.solution is Solution.Yes &&
                        moreThanOne(goals.asSequence()) ->
                    if (
                        solveConjunctionGoals(
                            mainRequest,
                            goals.drop(1),
                            goalFinalState.context,
                            goalResponse.solution.substitution - mainRequest.context.substitution,
                            allSideEffectsSoFar,
                            goalResponse.sideEffectManager as? SideEffectManagerImpl,
                            previousGoalsHadAlternatives || currentHasAlternatives
                        )
                    ) cutExecuted = true
                else ->
                    // yield only non-false responses or false responses when there are no open alternatives (because no more or cut)
                    if (goalResponse.solution !is Solution.No || (!previousGoalsHadAlternatives && !currentHasAlternatives) || cutExecuted)
                        yield(
                            mainRequest.replyWith(
                                goalResponse.copy(sideEffects = allSideEffectsSoFar)
                            )
                        )

            }
            if (cutExecuted || goalResponse.solution is Solution.Halt) return true // cut other alternatives of current and previous goals
        }
        return cutExecuted
    }
}
