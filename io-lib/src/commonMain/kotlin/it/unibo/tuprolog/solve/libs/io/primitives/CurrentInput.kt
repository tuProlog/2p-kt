package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrStream
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `current_input/1`: unifies the argument with the `$stream(...)` term of the current input
 * stream (the context's current input, or its standard input if none was [SetInput]) if the argument is unbound;
 * if it is already bound to a `$stream(...)` term, succeeds iff it already denotes that same channel.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if the argument names a well-formed stream term
 * for which no channel is currently open.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_or_alias`) if it is bound to something other
 * than a `$stream(...)` term.
 */
object CurrentInput : UnaryPredicate.NonBacktrackable<ExecutionContext>("current_input") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsVarOrStream(0)
        val currentChannel = context.inputChannels.let { it.current ?: it.stdIn }
        return when (channel) {
            null -> replyWith(mgu(first, currentChannel.streamTerm))
            currentChannel -> replySuccess()
            else -> replyFail()
        }
    }
}
