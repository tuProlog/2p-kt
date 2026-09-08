package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `at_end_of_stream/1`: succeeds iff the input stream identified by the first argument (an alias
 * or a `$stream(...)` term, see [IOPrimitiveUtils.ensuringArgumentIsInputChannel]) is closed or exhausted.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if the stream/alias does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 */
object AtEndOfStream1 : UnaryPredicate.Predicative<ExecutionContext>("at_end_of_stream") {
    override fun Solve.Request<ExecutionContext>.compute(first: Term): Boolean =
        ensuringArgumentIsInputChannel(0).let { it.isClosed || it.isOver }
}
