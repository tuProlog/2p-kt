package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils.newThrowSolveRequest
import it.unibo.tuprolog.solve.solver.statemachine.StateMachineUtils.toStateEnd
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of solving a selected Goal, if it is a primitive
 *
 * @author Enrico
 */
internal class StateGoalEvaluation(
        override val solveRequest: Solve.Request,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solveRequest, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val primitive = with(solveRequest) { context.libraries.primitives[signature] }

        primitive?.also {
            // primitive with correct signature present
            var responses: Sequence<Solve.Response>? = null
            try {
                // execute primitive
                responses = primitive(solveRequest)
            } catch (exception: HaltException) {
                // if primitive throws HaltException halt state-machine
                yield(StateEnd.Halt(solveRequest.copy(context = exception.context), executionStrategy, exception))
            } catch (prologError: PrologError) {
                // if primitive throws PrologError try to solve corresponding throw/1 request
                responses = SolverSLD().solve(solveRequest.newThrowSolveRequest(prologError))
            }

            responses?.forEach {
                // transform primitive responses to corresponding state-machine end states carrying modified context
                yield(it.solution.toStateEnd(solveRequest.copy(context = it.context), executionStrategy))

                if (it.solution is Solution.Halt) return@sequence // if halt reached, overall computation should stop
            }

        } ?: yield(StateRuleSelection(solveRequest, executionStrategy))
    }
}
