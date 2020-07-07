package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.StreamsSolver
import it.unibo.tuprolog.solve.exception.error.MetaError
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext
import it.unibo.tuprolog.solve.solver.newSolveRequest
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

/**
 * Implementation of primitive handling `findall/3` behaviour
 */
internal object FindAll : PrimitiveWrapper<StreamsExecutionContext>("findall", 3) {

    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        sequence {
            val firstArg = request.arguments[0]
            val secondArg = request.ensuringArgumentIsInstantiated(1).arguments[1]
            val thirdArg = request.arguments[2]

            val solutions = StreamsSolver.solveToResponses(request.newSolveRequest(secondArg as Struct)).toList()
            val error = solutions.asSequence().map { it.solution }.filterIsInstance<Solution.Halt>().firstOrNull()
            if (error != null) {
                yield(request.replyException(MetaError.of(request.context, error.exception)))
            }
            val mapped = solutions.asSequence().map { it.solution }
                .filterIsInstance<Solution.Yes>()
                .map { firstArg[it.substitution].freshCopy() }

            yield(
                request.replySuccess(
                    (request.context.substitution + thirdArg.mguWith(List.from(mapped))) as Substitution.Unifier
                )
            )
        }
}
