package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.statemachine.state.FinalState
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import it.unibo.tuprolog.solve.solver.statemachine.state.StateInit
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
internal class SolverSLD(
        override val startContext: ExecutionContext = ExecutionContext(
                Libraries(),
                emptyMap(),
                ClauseDatabase.empty(),
                ClauseDatabase.empty()
        ),
        private val executionStrategy: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : AbstractSolver(startContext) {

    override fun solve(goal: Struct): Sequence<Solution> =
            solve(Solve.Request(goal.extractSignature(), goal.argsList, startContext)).map { it.solution }

    /** Internal version of other [solve] method, that accepts raw requests and returns raw responses */
    internal fun solve(goalRequest: Solve.Request): Sequence<Solve.Response> = StateMachineExecutor
            .execute(StateInit(goalRequest, executionStrategy))
            .filterIsInstance<FinalState>()
            .filter { it.solveRequest.equalSignatureAndArgs(goalRequest) }
            .map { Solve.Response(it.toSolution(), it.solveRequest.context) }

    /** Utility method to map a [FinalState] to its corresponding [Solution] */
    private fun FinalState.toSolution() = when (this) {
        is StateEnd.True -> with(solveRequest) { Solution.Yes(signature, arguments, answerSubstitution) }
        is StateEnd.Halt -> with(solveRequest) { Solution.Halt(signature, arguments, exception) }
        else -> with(solveRequest) { Solution.No(signature, arguments) }
    }
}
