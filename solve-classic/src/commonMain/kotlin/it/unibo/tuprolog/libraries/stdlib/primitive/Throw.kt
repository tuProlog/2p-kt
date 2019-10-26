package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

object Throw : UnaryPredicate<ExecutionContext>("throw") {
    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
            sequenceOf(
                    request.ensuringAllArgumentsAreInstantiated()
                            .replyException(
                                    handleError(request.context, request.arguments[0] as Struct)
                            )
            )

    private fun handleError(context: ExecutionContext, error: Struct): TuPrologRuntimeException =
            when {
                error.functor == "error" && error.arity <= 2 -> {
                    PrologError.of(context = context, type = error[0] as Struct, extraData = if (error.arity > 1) error[1] else null)
                }
                else -> PrologError.of(context = context, type = error)
            }
}