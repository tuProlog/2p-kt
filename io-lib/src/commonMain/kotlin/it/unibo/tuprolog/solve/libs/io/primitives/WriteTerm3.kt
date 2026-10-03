package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsFormatter
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeTermAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * Implements ISO's `write_term/3`: like [WriteTerm2], but writing to the output stream identified by the first
 * argument (an alias or `$stream(...)` term) instead of the current output, with the term and options shifted to
 * the second and third arguments respectively.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first or third argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if the first argument does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the third argument is bound but not a list.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`write_option`) if an element of the third argument
 * is not one of the recognized option shapes.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object WriteTerm3 : TernaryRelation.NonBacktrackable<ExecutionContext>("write_term") {
    override val help: String =
        """
        `write_term(+Stream, @Term, +Options)`
        
        Writes `Term` to the output stream `Stream`, formatted according to `Options` and the current operators, succeeding deterministically. `Stream` must be an alias (e.g. `user_output`) or a `${'$'}stream(out, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an input stream a domain error (`stream_type`), where ISO prescribes a permission error. `Options` must be a list (an unbound `Options` raises a type error (`list`) rather than ISO's instantiation error) whose elements are `quoted(Bool)` (quote atoms and functors where needed), `ignore_ops(Bool)` (write operators in canonical functional notation) and `numbervars(Bool)` (write `'${'$'}VAR'(N)` terms as variable letters), with `Bool` being `true` or `false`; missing options default to `false`, and any other element raises a domain error (`write_option`). It fails, instead of raising an error, if the underlying channel has been closed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        val formatter = ensuringArgumentIsFormatter(2)
        return writeTermAndReply(channel, second, formatter)
    }
}
