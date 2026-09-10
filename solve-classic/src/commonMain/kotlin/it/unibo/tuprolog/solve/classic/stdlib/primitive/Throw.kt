package it.unibo.tuprolog.solve.classic.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * ISO `throw/1`: raises its argument as a [ResolutionException], to be looked for by an enclosing `catch/3` as
 * the state machine climbs the execution-context stack in `StateException`.
 *
 * If the argument is an `error(Type, Extra)` (or `error(Type)`) compound, it is wrapped into a [LogicError] of
 * the given `it.unibo.tuprolog.solve.exception.error` `Type`; any other term is wrapped into a generic
 * [MessageError] instead.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is an unbound variable.
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
            ensuringAllArgumentsAreInstantiated()
                .replyException(handleError(context, arguments[0])),
        )
}
