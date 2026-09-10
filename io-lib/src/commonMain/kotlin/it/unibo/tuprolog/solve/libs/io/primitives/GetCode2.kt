package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrCharCode
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readCodeAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `get_code/2`: reads (consuming it) the next character code from the input stream identified by
 * the first argument (an alias or `$stream(...)` term) and unifies it with the second, as `-1` at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object GetCode2 : BinaryRelation.NonBacktrackable<ExecutionContext>("get_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        ensuringArgumentIsVarOrCharCode(1)
        return readCodeAndReply(channel, second)
    }
}
