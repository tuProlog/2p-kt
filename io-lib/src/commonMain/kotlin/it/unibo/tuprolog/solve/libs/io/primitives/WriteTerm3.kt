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
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        val formatter = ensuringArgumentIsFormatter(1)
        return writeTermAndReply(channel, first, formatter)
    }
}
