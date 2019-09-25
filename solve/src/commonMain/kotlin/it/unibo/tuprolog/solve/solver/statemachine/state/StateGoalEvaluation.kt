package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils.newThrowSolveRequest
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineUtils.toFinalState
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of solving a selected Goal, if it is a primitive
 *
 * @author Enrico
 */
internal class StateGoalEvaluation(
        override val solveRequest: Solve.Request<ExecutionContextImpl>,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val primitive = with(solveRequest) { context.libraries.primitives[signature] }

        primitive?.also {
            // primitive with request signature present
            var responses: Sequence<Solve.Response>? = null
            try {
                responses = primitive(solveRequest) // execute primitive

            } catch (exception: HaltException) {

                yield(StateEnd.Halt(solveRequest.copy(context = exception.context), executionStrategy, exception))

            } catch (prologError: PrologError) {

                responses = SolverSLD().solve(solveRequest.newThrowSolveRequest(prologError)) // if primitive throws PrologError try to solve corresponding throw/1 request
            }

            responses?.forEach {

                yield(it.solution.toFinalState(solveRequest.copy(context = it.context!!), executionStrategy))

                if (it.solution is Solution.Halt) return@sequence // if halt reached, overall computation should stop
            }

        } ?: yield(StateRuleSelection(solveRequest, executionStrategy))
    }
}
