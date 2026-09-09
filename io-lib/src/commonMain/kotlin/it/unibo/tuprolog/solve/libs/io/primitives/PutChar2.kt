package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeCharAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `put_char/2`: writes the one-character atom second argument to the output stream identified by
 * the first (an alias or `$stream(...)` term).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if either argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if the first argument does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`character`) if the second argument is bound but not a
 * one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PutChar2 : BinaryRelation.NonBacktrackable<ExecutionContext>("put_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        ensuringArgumentIsChar(1)
        return writeCharAndReply(channel, second as Atom)
    }
}
