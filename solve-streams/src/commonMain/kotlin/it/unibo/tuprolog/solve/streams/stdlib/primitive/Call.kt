package it.unibo.tuprolog.solve.streams.stdlib.primitive

import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.StreamsSolver
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.isWellFormed
import it.unibo.tuprolog.solve.streams.solver.newSolveRequest
import it.unibo.tuprolog.solve.streams.solver.prepareForExecutionAsGoal
import it.unibo.tuprolog.solve.streams.solver.replyWith
import it.unibo.tuprolog.solve.streams.solver.resetCutWorkChanges

/**
 * Implementation of primitive handling `call/1` behaviour
 *
 * @author Enrico
 */
internal object Call : PrimitiveWrapper<StreamsExecutionContext>("call", 1) {
    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        request.ensuringAllArgumentsAreInstantiated().arguments.single().let { toBeCalledGoal ->
            when {
                toBeCalledGoal.isWellFormed() ->
                    StreamsSolver
                        .solveToResponses(
                            request.newSolveRequest(toBeCalledGoal.prepareForExecutionAsGoal()),
                        ).map {
                            request.replyWith(
                                it.copy(
                                    sideEffectManager =
                                        it.sideEffectManager
                                            .resetCutWorkChanges(request.context.sideEffectManager),
                                ),
                            )
                        }

                else -> throw TypeError(
                    message = "call/1 argument is neither a Variable nor a well-formed goal",
                    context = request.context,
                    expectedType = TypeError.Expected.CALLABLE,
                    culprit = toBeCalledGoal,
                )
            }
        }
}
