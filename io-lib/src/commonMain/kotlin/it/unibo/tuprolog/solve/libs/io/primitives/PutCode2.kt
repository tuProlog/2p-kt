package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeCodeAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `put_code/2`: writes the character coded by the integer second argument to the output stream
 * identified by the first (an alias or `$stream(...)` term).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if either argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if the first argument does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`character_code`) if the second argument is bound but
 * not an integer.
 * @throws it.unibo.tuprolog.solve.exception.error.RepresentationError (`character_code`) if it is an integer outside
 * the representable character-code range.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PutCode2 : BinaryRelation.NonBacktrackable<ExecutionContext>("put_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        ensuringArgumentIsCharCode(1)
        return writeCodeAndReply(channel, first as Integer)
    }
}
