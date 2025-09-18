package it.unibo.tuprolog.solve.streams.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.forEachWithLookahead
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.streams.StreamsSolver
import it.unibo.tuprolog.solve.streams.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.addWithNoDuplicates
import it.unibo.tuprolog.solve.streams.solver.moreThanOne
import it.unibo.tuprolog.solve.streams.solver.newSolveRequest
import it.unibo.tuprolog.solve.streams.solver.orderWithStrategy
import it.unibo.tuprolog.solve.streams.solver.prepareForExecutionAsGoal
import it.unibo.tuprolog.solve.streams.solver.replyWith
import it.unibo.tuprolog.solve.streams.solver.shouldExecuteThrowCut

/**
 * Implementation of primitive handling `','/2` behaviour
 *
 * @author Enrico
 */
internal object Conjunction : PrimitiveWrapper<StreamsExecutionContext>(Tuple.FUNCTOR, 2) {
    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        sequence {
            val subGoals =
                with(request) {
                    query
                        .castToTuple()
                        .toSequence()
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
                previousGoalsHadAlternatives = false,
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
        previousGoalsHadAlternatives: Boolean,
    ): Pair<Boolean, List<SideEffect>> {
        val goal = goals.first().apply(accumulatedSubstitutions).prepareForExecutionAsGoal()

        val goalRequest =
            mainRequest.newSolveRequest(
                goal,
                accumulatedSubstitutions,
                toPropagateContext,
                baseSideEffectManager = previousResponseSideEffectManager ?: mainRequest.context.sideEffectManager,
            )

        var cutExecuted = false
        var currentSideEffects = emptyList<SideEffect>()
        StreamsSolver.solveToFinalStates(goalRequest).forEachWithLookahead { goalFinalState, currentHasAlternatives ->
            val goalResponse = goalFinalState.solve
            if (Cut.functor == goal.functor || goalResponse.sideEffectManager?.shouldExecuteThrowCut() == true) {
                cutExecuted = true
            }

            currentSideEffects = currentSideEffects.addWithNoDuplicates(goalResponse.sideEffects)

            when {
                goalResponse.sideEffectManager?.shouldExecuteThrowCut() == false &&
                    goalResponse.solution is Solution.Yes &&
                    moreThanOne(goals.asSequence()) -> {
                    val sideEffectPair =
                        solveConjunctionGoals(
                            mainRequest,
                            goals.drop(1),
                            goalFinalState.context.apply(currentSideEffects),
                            goalResponse.solution.substitution - mainRequest.context.substitution,
                            currentSideEffects,
                            goalResponse.sideEffectManager as? SideEffectManagerImpl,
                            previousGoalsHadAlternatives || currentHasAlternatives,
                        )

                    if (sideEffectPair.first) {
                        cutExecuted = true
                    }

                    currentSideEffects = sideEffectPair.second
                }
                else ->
                    // yield only non-false responses or false responses when there are no open alternatives (because no more or cut)
                    if (
                        goalResponse.solution !is Solution.No ||
                        (!previousGoalsHadAlternatives && !currentHasAlternatives) ||
                        cutExecuted
                    ) {
                        yield(
                            mainRequest.replyWith(
                                goalResponse.copy(sideEffects = accumulatedSideEffects + currentSideEffects),
                            ),
                        )
                    }
            }
            // cut other alternatives of current and previous goals
            if (cutExecuted || goalResponse.solution is Solution.Halt) {
                return Pair(true, accumulatedSideEffects + currentSideEffects)
            }
        }
        return Pair(cutExecuted, accumulatedSideEffects + currentSideEffects)
    }
}
