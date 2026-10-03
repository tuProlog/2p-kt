package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Registered for ISO conformance, but **not implemented**: only text streams are supported, so byte-oriented
 * `get_byte/2` always raises a [it.unibo.tuprolog.solve.exception.error.SystemError]. Use [GetCode2]/[GetChar2] instead.
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object GetByte2 : BinaryRelation.NonBacktrackable<ExecutionContext>("get_byte") {
    override val help: String =
        """
        `get_byte(+Stream, ?Byte)`
        
        Registered for ISO conformance only: the current implementation supports text streams exclusively, so every call raises a system error regardless of its arguments; use `get_code/2` instead.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = notSupported()
}
