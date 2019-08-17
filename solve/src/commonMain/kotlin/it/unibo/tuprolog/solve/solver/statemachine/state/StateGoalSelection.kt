package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.prepareForExecution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.NotableFunctor
import it.unibo.tuprolog.solve.solver.SolverStrategies
import kotlinx.coroutines.CoroutineScope

/**
 * The state responsible of making the choice of which goal will be solved next
 *
 * @author Enrico
 */
internal class StateGoalSelection(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope,
        override val solverStrategies: SolverStrategies
) : AbstractTimedState(solveRequest, executionStrategy, solverStrategies) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val currentGoalStruct = with(solveRequest) { signature.withArgs(arguments) }

        when {
            // current goal is already demonstrated
            with(currentGoalStruct) { this != null && solverStrategies.successCheckStrategy(this) } ->
                yield(StateEnd.True(solveRequest, executionStrategy, solverStrategies))

            else -> with(solveRequest) {
                when {
                    // current goal is split in conjunction, disjunction or implication
                    signature.name in Clause.notableFunctors -> {
                        val subGoalsToBeSolved = goalsToSolveOrdered(arguments.asSequence(), solverStrategies::predicationChoiceStrategy)

                        val combinedSubSolutions = solveSubGoals(
                                subGoalsToBeSolved.first(),
                                subGoalsToBeSolved.last(),
                                NotableFunctor.valueOf(signature.name)
                        )

                        when (combinedSubSolutions) {
                            is Solution.Yes -> TODO("Extract actual substitution from solution and add it to current context of a StateEnd.True()")
                            is Solution.No -> yield(StateEnd.False(solveRequest, executionStrategy, solverStrategies))
                        }
                    }

                    // current goal is some other Structure
                    else -> {
                        // TODO: 17/08/2019 move to GoalEvaluationState
                    }
                }
            }
        }
    }

    private fun <P : Term> solveSubGoals(leftSubGoal: P, rightSubGoal: P, notableFunctor: NotableFunctor): Solution {

        // TODO: 17/08/2019 check for sub goals correctness, with Clause.bodyWellFormedVisitor
        // TODO: 17/08/2019 prepareForExecution should be called on arguments

        // Solver.sld(executionStrategy).solve(Solve.Request(Signature.fromIndicator(leftSubGoal.)!!))

        TODO()
    }


    /** Computes the ordered selection of goals according to provided selection strategy, run always on remaining goals */
    private fun <P : Term> goalsToSolveOrdered(goals: Sequence<P>, selectionStrategy: (Sequence<P>) -> P): Sequence<P> =
            mutableListOf<P>()
                    .also { selected ->
                        repeat(goals.count()) { selected += selectionStrategy(goals - selected) }
                    }.asSequence()


    /** Check whether the provided term is a well-formed predication */
    private fun <P : Term> subGoalWellFormed(subGoal: P): Boolean = subGoal.accept(Clause.bodyWellFormedVisitor)

    /**
     * Prepares the provided Goal for execution
     *
     * For example, the goal `A` is transformed, after preparation for execution, as the Term: `call(A)`
     */
    private fun <P : Term> subGoalPrepareForExecution(subGoal: P): P =
            // exploits "Clause" implementation of prepareForExecution() to do that
            Directive.of(subGoal).prepareForExecution().args.single().castTo()
}
