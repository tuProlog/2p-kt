package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readTermAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `read/1`: parses the next term from the current input stream (see
 * [IOPrimitiveUtils.currentInputChannel]) and unifies it with the argument. See [IOPrimitiveUtils.readTermAndReply]
 * for the shared implementation and its exceptions.
 *
 * Deviating from the ISO standard (which specifies unifying the argument with the atom `end_of_file`), this
 * implementation instead fails once the stream is exhausted or has no term currently available.
 *
 * ```prolog
 * ?- read(Term).
 * ```
 */
object Read1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("read") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response =
        readTermAndReply(currentInputChannel, first)
}
