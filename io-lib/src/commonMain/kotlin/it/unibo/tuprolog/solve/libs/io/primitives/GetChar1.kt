package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readCharAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `get_char/1`: reads (consuming it) the next character from the current input stream (see
 * [IOPrimitiveUtils.currentInputChannel]) and unifies it with the argument, as the `end_of_file` atom at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`in_character`) if the argument is bound to something
 * other than `end_of_file` or a one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object GetChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("get_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrChar(0)
        return readCharAndReply(currentInputChannel, first)
    }
}
