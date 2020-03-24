package it.unibo.tuprolog.solve.library.stdlib.primitive

import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.StreamsSolver
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.solver.*

/**
 * Implementation of primitive handling `call/1` behaviour
 *
 * @author Enrico
 */
internal object Call : PrimitiveWrapper<ExecutionContextImpl>("call", 1) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
        request.ensuringAllArgumentsAreInstantiated().arguments.single().let { toBeCalledGoal ->
            when {
                toBeCalledGoal.isWellFormed() ->
                    StreamsSolver.solve(request.newSolveRequest(toBeCalledGoal.prepareForExecutionAsGoal())).map {
                        request.replyWith(
                            it.copy(
                                sideEffectManager = it.sideEffectManager
                                    .resetCutWorkChanges(request.context.sideEffectManager)
                            )
                        )
                    }

                else -> throw TypeError(
                    message = "call/1 argument is neither a Variable nor a well-formed goal",
                    context = request.context,
                    expectedType = TypeError.Expected.CALLABLE,
                    actualValue = toBeCalledGoal
                )
            }
        }
}
