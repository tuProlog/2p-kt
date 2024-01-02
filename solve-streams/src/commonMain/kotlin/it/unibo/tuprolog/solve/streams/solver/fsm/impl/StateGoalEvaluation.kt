package it.unibo.tuprolog.solve.streams.solver.fsm.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.currentTimeInstant
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffect
import it.unibo.tuprolog.solve.streams.StreamsSolver
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.addWithNoDuplicates
import it.unibo.tuprolog.solve.streams.solver.fsm.State
import it.unibo.tuprolog.solve.streams.solver.getSideEffectManager
import it.unibo.tuprolog.solve.streams.solver.newSolveRequest
import it.unibo.tuprolog.solve.streams.stdlib.primitive.Throw

/**
 * State responsible of solving a selected Goal, if it is a primitive
 *
 * @author Enrico
 */
internal class StateGoalEvaluation(
    override val solve: Solve.Request<StreamsExecutionContext>,
) : AbstractTimedState(solve) {
    override fun behaveTimed(): Sequence<State> =
        sequence {
            val primitive = with(solve) { context.libraries.primitives[signature] }

            primitive?.also {
                // primitive with request signature present
                var responses: Sequence<Solve.Response>? = null
                try {
                    responses = primitive.solve(solve) // execute primitive
                } catch (exception: HaltException) {
                    yield(stateEndHalt(exception))
                } catch (logicError: LogicError) {
                    // if primitive throws LogicError try to solve corresponding throw/1 request

                    responses = StreamsSolver.solveToResponses(solve.newThrowSolveRequest(logicError))
                }

                var allSideEffectsSoFar = emptyList<SideEffect>()
                responses?.forEach {
                    allSideEffectsSoFar = allSideEffectsSoFar.addWithNoDuplicates(it.sideEffects)

                    yield(ifTimeIsNotOver(stateEnd(it.copy(sideEffects = allSideEffectsSoFar))))

                    if (it.solution is Solution.Halt) return@sequence // if halt reached, overall computation should stop
                }
            } ?: yield(StateRuleSelection(solve))
        }

    private companion object {
        /** Utility function to create "throw/1" solve requests */
        private fun Solve.Request<StreamsExecutionContext>.newThrowSolveRequest(error: LogicError) =
            newSolveRequest(
                Struct.of(Throw.functor, error.errorStruct),
                baseSideEffectManager = error.context.getSideEffectManager() ?: context.sideEffectManager,
                requestIssuingInstant = currentTimeInstant(),
            )
    }
}
