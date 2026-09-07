package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeTermAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `write_canonical/1`: formats the argument in canonical form (quoted, ignoring operator notation,
 * i.e. [TermFormatter.canonical]) and writes it to the current output stream (see
 * [IOPrimitiveUtils.currentOutputChannel]).
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object WriteCanonical1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("write_canonical") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response =
        writeTermAndReply(currentOutputChannel, first, TermFormatter.canonical())
}
