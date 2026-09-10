package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `close/1`: closes the stream identified by the first argument (an alias or `$stream(...)` term,
 * see [IOPrimitiveUtils.ensuringArgumentIsChannel]) and drops every alias, other than the reserved "current" one,
 * under which it was reachable.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_or_alias`) if it is neither an atom nor a
 * `$stream(...)` term.
 *
 * Fails, rather than erroring, if the channel is already closed.
 */
object Close1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("close") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsChannel(0)
        try {
            channel.close()
            return when (channel) {
                is InputChannel<*> -> {
                    replySuccess {
                        closeInputChannels(context.inputChannels.aliasesOf(channel as InputChannel<String>))
                    }
                }
                is OutputChannel<*> -> {
                    replySuccess {
                        closeOutputChannels(context.outputChannels.aliasesOf(channel as OutputChannel<String>))
                    }
                }
                else -> throw IllegalStateException("This should never happen")
            }
        } catch (_: IllegalStateException) {
            return replyFail()
        }
    }
}
