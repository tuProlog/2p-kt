package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeTermAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `writeq/2`: like [WriteEq1], but writing to the output stream identified by the first argument
 * (an alias or `$stream(...)` term) instead of the current output.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object WriteEq2 : BinaryRelation.NonBacktrackable<ExecutionContext>("writeq") {
    override val help: String =
        """
        `writeq(+Stream, @Term)`
        
        Writes `Term` to the output stream `Stream` so that it can be read back, quoting atoms and functors where needed and writing `'${'$'}VAR'(N)` terms as variable letters; the current implementation formats operators according to the default operator table rather than the current one. It succeeds deterministically. `Stream` must be an alias (e.g. `user_output`) or a `${'$'}stream(out, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an input stream a domain error (`stream_type`), where ISO prescribes a permission error. It fails, instead of raising an error, if the underlying channel has been closed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        return writeTermAndReply(channel, second, TermFormatter.readable())
    }
}
