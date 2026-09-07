package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Registered for ISO conformance, but **not implemented**: only text streams are supported, so byte-oriented
 * `put_byte/2` always raises a [it.unibo.tuprolog.solve.exception.error.SystemError]. Use
 * [PutCode2]/[PutChar2] instead.
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object PutByte2 : BinaryRelation.NonBacktrackable<ExecutionContext>("put_byte") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = notSupported()
}
