package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineExecutor
import it.unibo.tuprolog.solve.solver.statemachine.state.FinalState
import it.unibo.tuprolog.solve.solver.statemachine.state.StateInit
import it.unibo.tuprolog.solve.solver.statemachine.state.SuccessFinalState
import kotlinx.coroutines.CoroutineScope

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
internal class SolverSLD(private val executionStrategy: CoroutineScope) : AbstractSolver() {

    override fun solve(goal: Solve.Request): Sequence<Solve.Response> =
            StateMachineExecutor.execute(StateInit(goal, executionStrategy, solverStrategies))
                    .filterIsInstance<FinalState>()
                    .filter { it.solveRequest.equalSignatureAndArgs(goal) }
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

        else -> with(finalState.solveRequest) {
            Solution.No(signature, arguments)
        }
    }

}
