package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils.newThrowSolveRequest
import it.unibo.tuprolog.solve.solver.statemachine.stateEnd
import it.unibo.tuprolog.solve.solver.statemachine.stateEndHalt
import kotlinx.coroutines.CoroutineScope

/**
 * State responsible of solving a selected Goal, if it is a primitive
 *
 * @author Enrico
 */
internal class StateGoalEvaluation(
        override val solve: Solve.Request<ExecutionContextImpl>,
        override val executionStrategy: CoroutineScope
) : AbstractTimedState(solve, executionStrategy) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val primitive = with(solve) { context.libraries.primitives[signature] }

        primitive?.also {
            // primitive with request signature present
            var responses: Sequence<Solve.Response>? = null
            try {
                responses = primitive(solve) // execute primitive

            } catch (exception: HaltException) {

                yield(stateEndHalt(exception, contextImpl = exception.context as ExecutionContextImpl))

            } catch (prologError: PrologError) {

                responses = SolverSLD().solve(solve.newThrowSolveRequest(prologError)) // if primitive throws PrologError try to solve corresponding throw/1 request
            }

            responses?.forEach {

                yield(stateEnd(it))

                if (it.solution is Solution.Halt) return@sequence // if halt reached, overall computation should stop
            }

        } ?: yield(StateRuleSelection(solve, executionStrategy))
    }
}
