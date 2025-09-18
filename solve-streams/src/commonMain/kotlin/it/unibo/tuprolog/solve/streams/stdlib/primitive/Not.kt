package it.unibo.tuprolog.solve.streams.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.streams.StreamsSolver
import it.unibo.tuprolog.solve.streams.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.streams.solver.newSolveRequest
import it.unibo.tuprolog.solve.streams.solver.replyWith

/**
 * Implementation of primitive handling `'\+'/1` behaviour
 *
 * @author Enrico
 */
internal object Not : PrimitiveWrapper<StreamsExecutionContext>("\\+", 1) {
    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        sequence {
            val goalArgument = request.arguments.single()

            StreamsSolver
                .solveToResponses(
                    request.newSolveRequest(Struct.of(Call.functor, goalArgument)),
                ).forEach { goalResponse ->
                    when (goalResponse.solution) {
                        is Solution.Yes -> {
                            yield(request.replyFail())
                            return@sequence
                        }

                        is Solution.No -> yield(request.replySuccess(request.context.substitution))

                        else -> yield(request.replyWith(goalResponse))
                    }
                }
        }
}
