package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.AbstractSolver
import it.unibo.tuprolog.solve.solver.statemachine.state.FinalState
import it.unibo.tuprolog.solve.solver.statemachine.state.State
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import it.unibo.tuprolog.solve.solver.statemachine.state.StateInit
import kotlinx.coroutines.CoroutineScope

/**
 * Default implementation of SLD (*Selective Linear Definite*) solver, exploring the search tree
 *
 * @author Enrico
 */
internal class SolverSLD(private val executionStrategy: CoroutineScope) : AbstractSolver() {

    override fun solve(goal: Solve.Request): Sequence<Solve.Response> =
            stateMachineExecution(StateInit(goal, executionStrategy, solverStrategies))
                    .filterIsInstance<FinalState>()
                    .map {
                        Solve.Response(
                                finalStateToSolutionMapping(it),
                                it.solveRequest.context // should this be omitted?? or replaced by a dedicated property in FinalState?
                        )
                    }

    /** Internal method that executes the state-machine */
    private fun stateMachineExecution(state: State): Sequence<State> =
            generateSequence(state.behave()) { nextStates ->
                // TODO cut execution should be catch here and make subsequent evaluation to be cancelled
                sequence { nextStates.forEach { yield(stateMachineExecution(it)) } }.flatten()
            }.flatten()

    /** Utility method to map a final state to its corresponding Solution */
    private fun finalStateToSolutionMapping(finalState: FinalState) = when (finalState) {
        is StateEnd.True,
        is StateEnd.TrueWithChoicePoint ->
            with(finalState.solveRequest) {
                Solution.Yes(signature, arguments, finalState.answerSubstitution)
            }

        else -> with(finalState.solveRequest) {
            Solution.No(signature, arguments)
        }
    }

}
