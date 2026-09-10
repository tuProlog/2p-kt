package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readCharAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `get_char/2`: reads (consuming it) the next character from the input stream identified by the
 * first argument (an alias or `$stream(...)` term) and unifies it with the second, as `end_of_file` at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`in_character`) if the second argument is bound to
 * something other than `end_of_file` or a one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object GetChar2 : BinaryRelation.NonBacktrackable<ExecutionContext>("get_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        ensuringArgumentIsVarOrChar(1)
        return readCharAndReply(channel, second)
    }
}
