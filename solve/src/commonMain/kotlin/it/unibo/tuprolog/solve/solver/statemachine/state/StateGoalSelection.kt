package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.Solve
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

                        // TODO: 17/08/2019 check for sub goals correctness, with Clause.bodyWellFormedVisitor
                        // TODO: 17/08/2019 prepareForExecution should be called on arguments

                        // maybe should be a good idea to transform temporarily the notable subgoal into a Clause and use its methods directly

                        // TODO: 17/08/2019 depending on the specific functor the solution given from this state should change
                    }

                    // current goal is some other Structure
                    else -> {
                        // TODO: 17/08/2019 move to GoalEvaluationState
                    }
                }
            }
        }
    }

    /** Computes the ordered selection of goals according to provided selection strategy, run always on remaining goals */
    private fun <P : Term> goalsToSolveOrdered(goals: Sequence<P>, selectionStrategy: (Sequence<P>) -> P): Sequence<P> =
            mutableListOf<P>()
                    .also { selected ->
                        repeat(goals.count()) { selected += selectionStrategy(goals - selected) }
                    }.asSequence()

}
