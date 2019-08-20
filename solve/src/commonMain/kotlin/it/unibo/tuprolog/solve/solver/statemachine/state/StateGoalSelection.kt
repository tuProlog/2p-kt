package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.primitive.Signature
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
        val currentGoal = with(solveRequest) { signature.withArgs(arguments) }

        when {
            // current goal is already demonstrated
            with(currentGoal) { this != null && solverStrategies.successCheckStrategy(this, solveRequest.context) } ->
                yield(StateEnd.True(solveRequest, executionStrategy, solverStrategies))

            else -> when {
                // vararg primitive
                currentGoal == null ->
                    yield(StateGoalEvaluation(solveRequest, executionStrategy, solverStrategies))

                goalWellFormed(currentGoal) -> {
                    val preparedGoal = prepareGoalForExecution(currentGoal)

                    yield(StateGoalEvaluation(
                            solveRequest.copy(
                                    signature = Signature.fromIndicator(preparedGoal.indicator)!!,
                                    arguments = preparedGoal.argsList
                            ),
                            executionStrategy,
                            solverStrategies
                    ))
                }

                // goal non well-formed
                else ->
                    yield(StateEnd.False(solveRequest, executionStrategy, solverStrategies))
            }
        }
    }

    /** Check whether the provided term is a well-formed predication */
    private fun <P : Term> goalWellFormed(subGoal: P): Boolean = subGoal.accept(Clause.bodyWellFormedVisitor)

    /**
     * Prepares the provided Goal for execution
     *
     * For example, the goal `A` is transformed, after preparation for execution, as the Term: `call(A)`
     */
    private fun <P : Term> prepareGoalForExecution(subGoal: P): Struct =
            // exploits "Clause" implementation of prepareForExecution() to do that
            Directive.of(subGoal).prepareForExecution().args.single().castTo()
}
