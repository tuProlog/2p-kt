package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrCharCode
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.peekCodeAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `peek_code/1`: unifies the argument with the code of the next character of the current input
 * stream (see [IOPrimitiveUtils.currentInputChannel]) *without* consuming it, as `-1` at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError if the argument is bound but not an integer.
 * @throws it.unibo.tuprolog.solve.exception.error.RepresentationError (`character_code`) if it is an integer outside
 * the representable character-code range.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PeekCode1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("peek_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrCharCode(0)
        return peekCodeAndReply(currentInputChannel, first)
    }
}
