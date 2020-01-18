package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.exception.prologerror.MessageError

object Throw : UnaryPredicate<ExecutionContext>("throw") {
    override fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response> =
        sequenceOf(
            request.ensuringAllArgumentsAreInstantiated()
                .replyException(
                    handleError(request.context, request.arguments[0])
                )
        )

    private fun handleError(context: ExecutionContext, error: Term): TuPrologRuntimeException =
        when {
            error is Struct && error.functor == "error" && error.arity in 1..2 -> {
                PrologError.of(
                    context = context,
                    type = error[0] as Struct,
                    extraData = if (error.arity > 1) error[1] else null
                )
            }
            else -> MessageError.of(error, context)
        }
}