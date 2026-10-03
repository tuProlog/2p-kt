package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `nl/1`: writes a newline character to the output stream identified by the argument (an alias or
 * `$stream(...)` term).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object Nl1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("nl") {
    override val help: String =
        """
        `nl(+Stream)`
        
        Writes a newline character to the output stream `Stream`, succeeding deterministically (`nl/0`, writing to the current output, is provided by `prolog.lang`). `Stream` must be an alias (e.g. `user_output`) or a `${'$'}stream(out, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an input stream a domain error (`stream_type`), where ISO prescribes a permission error. It fails, instead of raising an error, if the underlying channel has been closed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        return try {
            channel.write("\n")
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
