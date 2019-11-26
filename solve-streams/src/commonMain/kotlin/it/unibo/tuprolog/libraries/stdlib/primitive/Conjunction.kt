package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.StreamsSolver
import it.unibo.tuprolog.solve.forEachWithLookahead
import it.unibo.tuprolog.solve.solver.*

/**
 * Implementation of primitive handling `','/2` behaviour
 *
 * @author Enrico
 */
internal object Conjunction : PrimitiveWrapper<ExecutionContextImpl>(Tuple.FUNCTOR, 2) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
        sequence {
            val subGoals = with(request) {
                query.`as`<Tuple>().toSequence()
                    .orderWithStrategy(context, context.solverStrategies::predicationChoiceStrategy)
                    .toList()
            }

            solveConjunctionGoals(
                request,
                subGoals,
                Substitution.empty(),
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
        mainRequest: Solve.Request<ExecutionContextImpl>,
        goals: Iterable<Term>,
        accumulatedSubstitutions: Substitution,
        previousResponseSideEffectManager: SideEffectManagerImpl?,
        previousGoalsHadAlternatives: Boolean
    ): Boolean {
        val goal = goals.first().apply(accumulatedSubstitutions).prepareForExecutionAsGoal()

        val goalRequest = mainRequest.newSolveRequest(
            goal,
            accumulatedSubstitutions,
            baseSideEffectManager = previousResponseSideEffectManager ?: mainRequest.context.sideEffectManager
        )

        var cutExecuted = false
        StreamsSolver.solve(goalRequest).forEachWithLookahead { goalResponse, currentHasAlternatives ->
            if (Cut.functor == goal.functor || goalResponse.sideEffectManager?.shouldExecuteThrowCut() == true)
                cutExecuted = true

            when {
                goalResponse.sideEffectManager?.shouldExecuteThrowCut() == false &&
                        goalResponse.solution is Solution.Yes &&
                        moreThanOne(goals.asSequence()) ->
                    if (
                        solveConjunctionGoals(
                            mainRequest,
                            goals.drop(1),
                            goalResponse.solution.substitution - mainRequest.context.substitution,
                            goalResponse.sideEffectManager as? SideEffectManagerImpl,
                            previousGoalsHadAlternatives || currentHasAlternatives
                        )
                    ) cutExecuted = true
                else ->
                    // yield only non-false responses or false responses when there are no open alternatives (because no more or cut)
                    if (goalResponse.solution !is Solution.No || (!previousGoalsHadAlternatives && !currentHasAlternatives) || cutExecuted)
                        yield(mainRequest.replyWith(goalResponse))

            }
            if (cutExecuted || goalResponse.solution is Solution.Halt) return true // cut other alternatives of current and previous goals
        }
        return cutExecuted
    }
}
