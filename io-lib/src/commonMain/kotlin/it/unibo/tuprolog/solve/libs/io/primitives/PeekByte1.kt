package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Registered for ISO conformance, but **not implemented**: only text streams are supported, so byte-oriented
 * `peek_byte/1` always raises a [it.unibo.tuprolog.solve.exception.error.SystemError]. Use
 * [PeekCode1]/[PeekChar1] instead.
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object PeekByte1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("peek_byte") {
    override val help: String =
        """
        `peek_byte(?Byte)`
        
        Registered for ISO conformance only: the current implementation supports text streams exclusively, so every call raises a system error regardless of its arguments; use `peek_code/1` instead.

        **Examples**

        ```prolog
        ?- peek_byte(B).
        throws error(system_error, _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response = notSupported()
}
