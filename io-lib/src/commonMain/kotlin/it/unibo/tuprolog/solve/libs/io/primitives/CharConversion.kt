package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Registered for ISO conformance, but **not implemented**: this implementation does not support a character
 * conversion table, so calling `char_conversion/2` always raises a
 * [it.unibo.tuprolog.solve.exception.error.SystemError].
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object CharConversion : BinaryRelation.NonBacktrackable<ExecutionContext>("char_conversion") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = notSupported()
}
