package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readTermAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * Implements ISO's `read_term/3`: like [ReadTerm2], but reading from the stream identified by the first argument
 * (an alias or `$stream(...)` term) instead of the current input.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the third argument is bound but not a list.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`read_option`) if an element of the third argument is
 * not one of the recognized option shapes.
 * @throws it.unibo.tuprolog.solve.exception.error.SyntaxError if the stream's next term is malformed Prolog syntax.
 */
object ReadTerm3 : TernaryRelation.NonBacktrackable<ExecutionContext>("read_term") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        return readTermAndReply(channel, second, lastIsInfoList = true)
    }
}
