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
    override val help: String =
        """
        `close(+Stream)`
        
        Closes the stream `Stream`, given as an alias or a `${'$'}stream(Direction, Id)` term, and unregisters every alias it was reachable through, succeeding deterministically. It raises an instantiation error if `Stream` is unbound, an existence error if it does not denote an open stream, and a domain error (`stream_or_alias`) if it is a compound term other than a stream term. It fails, instead of raising an error, if the underlying channel has been closed.

        **Examples**

        ```prolog
        ?- close(S).
        throws error(instantiation_error, _).

        ?- close(no_such_stream).
        throws error(existence_error(source_sink, no_such_stream), _).

        ?- close(foo(bar)).
        throws error(domain_error(stream_or_alias, foo(bar)), _).
        ```
        """.trimIndent()

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
