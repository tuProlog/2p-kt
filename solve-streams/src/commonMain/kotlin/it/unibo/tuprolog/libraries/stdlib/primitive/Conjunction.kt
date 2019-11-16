package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.SolverSLD
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
                request, request, subGoals, request.context.sideEffectManager, Substitution.empty(),
                previousGoalsHadAlternatives = false
            )

//            val (leftSubGoal, rightSubGoal) = with(request) {
//                arguments.asSequence().orderWithStrategy(context, context.solverStrategies::predicationChoiceStrategy)
//            }.toList()
//
//            val leftSubSolveRequest = request.newSolveRequest(leftSubGoal.prepareForExecutionAsGoal())
//
//            var cutExecuted = false
//            SolverSLD.solve(leftSubSolveRequest).forEachWithLookahead { leftResponse, hasLHSAlternatives ->
//                if (leftResponse.sideEffectManager?.shouldCutExecuteInPrimitive() == true)
//                    cutExecuted = true
//
//                when (leftResponse.solution) {
//                    is Solution.Yes -> {
//                        val rightSubSolveRequest = leftSubSolveRequest.newSolveRequest(
//                            rightSubGoal.apply(leftResponse.solution.substitution).prepareForExecutionAsGoal(),
//                            leftResponse.solution.substitution - leftSubSolveRequest.context.substitution,
//                            leftResponse.sideEffectManager as? SideEffectManagerImpl,
//                            logicalParentRequest = request
//                        )
//
//                        SolverSLD.solve(rightSubSolveRequest)
//                            .forEachWithLookahead { rightResponse, hasRHSAlternatives ->
//                                if (rightResponse.sideEffectManager?.shouldCutExecuteInPrimitive() == true)
//                                    cutExecuted = true
//
//                                // yield only non-false responses or false responses when there are no open alternatives (because no more or cut)
//                                if (rightResponse.solution !is Solution.No || (!hasLHSAlternatives && !hasRHSAlternatives) || cutExecuted)
//                                    yield(request.replyWith(rightResponse))
//
//                            }
//                    }
//                    else -> {
//                        // yield only non-false responses or false responses when there are no open alternatives (because no more or cut)
//                        if (leftResponse.solution !is Solution.No || !hasLHSAlternatives || cutExecuted)
//                            yield(request.replyWith(leftResponse))
//                    }
//                }
//                if (cutExecuted) return@sequence
//            }
        }

    private suspend fun SequenceScope<Solve.Response>.solveConjunctionGoals(
        mainRequest: Solve.Request<ExecutionContextImpl>,
        previousGoalRequest: Solve.Request<ExecutionContextImpl>,
        goals: Iterable<Term>,
        previousResponseSideEffectManager: SideEffectManagerImpl?,
        accumulatedSubstitutions: Substitution,
        previousGoalsHadAlternatives: Boolean
    ): Boolean {
        val goal = goals.first().apply(accumulatedSubstitutions).prepareForExecutionAsGoal()

        val goalRequest = previousGoalRequest.newSolveRequest(
            goal,
            accumulatedSubstitutions - previousGoalRequest.context.substitution,
            baseSideEffectManager = previousResponseSideEffectManager,
            logicalParentRequest = mainRequest
        )

        var cutExecuted = false
        SolverSLD.solve(goalRequest).forEachWithLookahead { goalResponse, currentHasAlternatives ->
            // TODO: 17/11/2019 a cleaner way to detect cut execution should be added through SideEffectManager
            if ("!" == goal.functor || goalResponse.sideEffectManager?.shouldExecuteThrowCut() == true)
                cutExecuted = true

            when {
                goalResponse.solution is Solution.Yes && moreThanOne(goals.asSequence()) ->
                    if (
                        solveConjunctionGoals(
                            mainRequest,
                            goalRequest,
                            goals.drop(1),
                            goalResponse.sideEffectManager as? SideEffectManagerImpl,
                            goalResponse.solution.substitution,
                            previousGoalsHadAlternatives || currentHasAlternatives
                        )
                    ) cutExecuted = true
                else ->
                    // yield only non-false responses or false responses when there are no open alternatives (because no more or cut)
                    if (goalResponse.solution !is Solution.No || (!previousGoalsHadAlternatives && !currentHasAlternatives) || cutExecuted)
                        yield(mainRequest.replyWith(goalResponse))

            }
            if (cutExecuted) return true
        }
        return cutExecuted
    }
}
