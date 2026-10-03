package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Registered for ISO conformance, but **not implemented**: only text streams are supported, so byte-oriented
 * `peek_byte/2` always raises a [it.unibo.tuprolog.solve.exception.error.SystemError]. Use
 * [PeekCode2]/[PeekChar2] instead.
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object PeekByte2 : BinaryRelation.NonBacktrackable<ExecutionContext>("peek_byte") {
    override val help: String =
        """
        `peek_byte(+Stream, ?Byte)`
        
        Registered for ISO conformance only: the current implementation supports text streams exclusively, so every call raises a system error regardless of its arguments; use `peek_code/2` instead.

        **Examples**

        ```prolog
        ?- peek_byte(user_input, B).
        throws error(system_error, _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = notSupported()
}
