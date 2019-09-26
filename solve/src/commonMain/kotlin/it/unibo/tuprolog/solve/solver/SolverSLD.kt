package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.statemachine.state.FinalState
import it.unibo.tuprolog.solve.solver.statemachine.state.StateInit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
internal class SolverSLD(
        override val startContext: ExecutionContextImpl = ExecutionContextImpl(),
        private val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : AbstractSolver(startContext) {

    override fun solve(goal: Struct): Sequence<Solution> =
            solve(Solve.Request(goal.extractSignature(), goal.argsList, goal, startContext))
                    .map { it.solution.withOnlyAnswerSubstitution() }

    /** Internal version of other [solve] method, that accepts raw requests and returns raw responses */
    internal fun solve(goalRequest: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> = StateMachineExecutor
            .execute(StateInit(goalRequest, executionStrategy))
            .filterIsInstance<FinalState>()
            .filter { it.solve.solution.query == goalRequest.query }
            .map { it.solve }

    // this should become useless when substitutions will be cleaned, while performing resolution
    /** Utility function to calculate answerSubstitution on Solution.Yes */
    private fun Solution.withOnlyAnswerSubstitution() = when (this) {
        is Solution.Yes ->
            // reduce substitution variable chains
            copy(substitution = SolverUtils.reduceAndFilterSubstitution(substitution, query.variables))
        else -> this
    }
}
