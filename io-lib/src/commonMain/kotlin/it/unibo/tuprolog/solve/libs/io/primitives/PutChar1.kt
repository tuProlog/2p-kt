package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeCharAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `put_char/1`: writes the one-character atom argument to the current output stream (see
 * [IOPrimitiveUtils.currentOutputChannel]).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`character`) if it is bound but not a one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PutChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("put_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsChar(0)
        return writeCharAndReply(currentOutputChannel, first as Atom)
    }
}
