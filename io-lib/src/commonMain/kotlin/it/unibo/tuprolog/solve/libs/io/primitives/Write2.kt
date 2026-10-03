package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeTermAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `write/2`: formats the second argument (unquoted, with operator notation, and expanding
 * `'$VAR'(N)` terms as variable names, i.e. [TermFormatter.default]) and writes it to the output stream identified
 * by the first argument (an alias or `$stream(...)` term).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object Write2 : BinaryRelation.NonBacktrackable<ExecutionContext>("write") {
    override val help: String =
        """
        `write(+Stream, @Term)`
        
        Writes `Term` to the output stream `Stream` as `write/1` does: unquoted, using the current operators, and writing `'${'$'}VAR'(N)` terms as variable letters. It succeeds deterministically. `Stream` must be an alias (e.g. `user_output`) or a `${'$'}stream(out, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an input stream a domain error (`stream_type`), where ISO prescribes a permission error. It fails, instead of raising an error, if the underlying channel has been closed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        return writeTermAndReply(channel, second, TermFormatter.default(context.operators))
    }
}
