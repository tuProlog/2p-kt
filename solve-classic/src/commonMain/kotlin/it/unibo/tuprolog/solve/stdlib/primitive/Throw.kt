package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Throw : UnaryPredicate<ExecutionContext>("throw") {
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

    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        return sequenceOf(
            ensuringAllArgumentsAreInstantiated()
                .replyException(
                    handleError(context, arguments[0])
                )
        )
    }
}