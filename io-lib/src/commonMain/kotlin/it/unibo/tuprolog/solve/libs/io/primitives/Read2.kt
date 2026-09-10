package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readTermAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `read/2`: parses the next term from the input stream identified by the first argument (an alias
 * or `$stream(...)` term) and unifies it with the second. See [IOPrimitiveUtils.readTermAndReply] for the shared
 * implementation (including the same end-of-stream-fails deviation from ISO documented on [Read1]).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.SyntaxError if the stream's next term is malformed Prolog syntax.
 */
object Read2 : BinaryRelation.NonBacktrackable<ExecutionContext>("read") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        return readTermAndReply(channel, second)
    }
}
