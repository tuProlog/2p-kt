package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Registered for ISO conformance, but **not implemented**: since [CharConversion] is unsupported, there is never
 * any conversion to enumerate, so `current_char_conversion/2` always raises a
 * [it.unibo.tuprolog.solve.exception.error.SystemError].
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object CurrentCharConversion : BinaryRelation.NonBacktrackable<ExecutionContext>("current_char_conversion") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = notSupported()
}
