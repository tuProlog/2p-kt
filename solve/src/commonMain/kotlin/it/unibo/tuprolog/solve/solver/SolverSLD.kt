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
import it.unibo.tuprolog.solve.solver.statemachine.state.SuccessFinalState
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
    internal fun solve(goalRequest: Solve.Request): Sequence<Solve.Response> =
            StateMachineExecutor.execute(StateInit(goalRequest, executionStrategy))
                    .filterIsInstance<FinalState>()
                    .filter { it.solveRequest.equalSignatureAndArgs(goalRequest) }
                    .map {
                        Solve.Response(
                                finalStateToSolutionMapping(it),
                                it.solveRequest.context // should this be omitted?? or replaced by a dedicated property in FinalState?
                        )
                    }

    /** Utility method to map a final state to its corresponding Solution */
    private fun finalStateToSolutionMapping(finalState: FinalState) = when (finalState) {
        is SuccessFinalState -> with(finalState.solveRequest) {
            Solution.Yes(signature, arguments, finalState.answerSubstitution)
        }

        is StateEnd.Halt -> with(finalState.solveRequest) {
            Solution.Halt(signature, arguments, finalState.exception)
        }

        else -> with(finalState.solveRequest) {
            Solution.No(signature, arguments)
        }
    }

}
