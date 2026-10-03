package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `flush_output/1`: forces buffered writes on the output stream identified by the argument (an
 * alias or `$stream(...)` term) to reach their destination.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object FlushOutput : UnaryPredicate.NonBacktrackable<ExecutionContext>("flush_output") {
    override val help: String =
        """
        `flush_output(+Stream)`
        
        Forces any buffered output of the output stream `Stream` to be written to its destination, succeeding deterministically. `Stream` must be an alias (e.g. `user_output`) or a `${'$'}stream(out, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an input stream a domain error (`stream_type`), where ISO prescribes a permission error. It fails, instead of raising an error, if the underlying channel has been closed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        return try {
            channel.flush()
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
