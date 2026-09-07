package it.unibo.tuprolog.solve.concurrent.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * `throw/1`: raises its argument as a [it.unibo.tuprolog.solve.exception.ResolutionException], to be caught by an
 * enclosing `catch/3` (see [it.unibo.tuprolog.solve.concurrent.fsm.StateException]) or, if none catches it, to
 * halt the branch that raised it (see [it.unibo.tuprolog.solve.concurrent.fsm.StateHalt]). Behaves like the
 * `:solve-classic`/`:solve-streams` implementations of `throw/1`: an `error(Type, Extra)`-shaped argument becomes a
 * [it.unibo.tuprolog.solve.exception.LogicError] of the corresponding [it.unibo.tuprolog.solve.exception.error]
 * subtype, anything else becomes a generic [it.unibo.tuprolog.solve.exception.error.MessageError].
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is not instantiated.
 */
object Throw : UnaryPredicate<ExecutionContext>("throw") {
    private fun handleError(
        context: ExecutionContext,
        error: Term,
    ): ResolutionException =
        when {
            error is Struct && error.functor == "error" && error.arity in 1..2 -> {
                LogicError.of(
                    context = context,
                    type = error[0] as Struct,
                    extraData = if (error.arity > 1) error[1] else null,
                )
            }
            else -> MessageError.of(error, context)
        }

    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> =
        sequenceOf(
            ensuringAllArgumentsAreInstantiated().replyException(handleError(context, arguments[0])),
        )
}
