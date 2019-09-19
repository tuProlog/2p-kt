package it.unibo.tuprolog.solve.solver.statemachine.state

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils.newThrowSolveRequest
import kotlinx.coroutines.CoroutineScope
/**
 * State responsible of solving a selected Goal
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
            var responses: Sequence<Solve.Response>? = null
            try {
                responses = primitive(solveRequest)
            } catch (exception: HaltException) {
                yield(StateEnd.Halt(solveRequest.copy(context = exception.context), executionStrategy, exception))
            } catch (prologError: PrologError) {
                responses = SolverSLD().solve(solveRequest.newThrowSolveRequest(prologError))
            }

            responses?.forEach {
                when (it.solution) {
                    is Solution.Yes ->
                        yield(StateEnd.True(
                                solveRequest.copy(context = it.context),
                                executionStrategy
                        ))

                    is Solution.Halt -> {
                        yield(StateEnd.Halt(
                                solveRequest.copy(context = it.context),
                                executionStrategy,
                                it.solution.exception
                        ))
                        return@sequence // if halt reached, overall computation should stop
                    }

                    is Solution.No ->
                        yield(StateEnd.False(
                                solveRequest.copy(context = it.context),
                                executionStrategy
                        ))
                }
            }

        } ?: yield(StateRuleSelection(solveRequest, executionStrategy))
    }
}
