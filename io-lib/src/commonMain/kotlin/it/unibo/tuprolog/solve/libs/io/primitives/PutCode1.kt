package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeCodeAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `put_code/1`: writes the character coded by the integer argument to the current output stream
 * (see [IOPrimitiveUtils.currentOutputChannel]).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`character_code`) if it is bound but not an integer.
 * @throws it.unibo.tuprolog.solve.exception.error.RepresentationError (`character_code`) if it is an integer outside
 * the representable character-code range.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PutCode1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("put_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsCharCode(0)
        return writeCodeAndReply(currentOutputChannel, first as Integer)
    }
}
