package it.unibo.tuprolog.solve.solver.fsm.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.StreamsSolver
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.library.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.solver.fsm.State
import it.unibo.tuprolog.solve.solver.getSideEffectManager
import it.unibo.tuprolog.solve.solver.newSolveRequest

/**
 * State responsible of solving a selected Goal, if it is a primitive
 *
 * @author Enrico
 */
internal class StateGoalEvaluation(
    override val solve: Solve.Request<StreamsExecutionContext>
) : AbstractTimedState(solve) {

    override fun behaveTimed(): Sequence<State> = sequence {
        val primitive = with(solve) { context.libraries.primitives[signature] }

        primitive?.also {
            // primitive with request signature present
            var responses: Sequence<Solve.Response>? = null
            try {
                responses = primitive(solve) // execute primitive

            } catch (exception: HaltException) {

                yield(stateEndHalt(exception))

            } catch (prologError: PrologError) {
                // if primitive throws PrologError try to solve corresponding throw/1 request

                responses = StreamsSolver.solveToResponses(solve.newThrowSolveRequest(prologError))
            }

            responses?.forEach {

                yield(stateEnd(it))

                if (it.solution is Solution.Halt) return@sequence // if halt reached, overall computation should stop
            }

        } ?: yield(StateRuleSelection(solve))
    }

    private companion object {

        /** Utility function to create "throw/1" solve requests */
        private fun Solve.Request<StreamsExecutionContext>.newThrowSolveRequest(error: PrologError) =
            newSolveRequest(
                Struct.of(Throw.functor, error.errorStruct),
                baseSideEffectManager = error.context.getSideEffectManager() ?: context.sideEffectManager
            )
    }
}
