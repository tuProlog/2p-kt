package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Registered for ISO conformance, but **not implemented**: `close/2` (`close/1` plus an options list, e.g.
 * `force(true)`) always raises a [it.unibo.tuprolog.solve.exception.error.SystemError]; use [Close1] instead.
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object Close2 : BinaryRelation.NonBacktrackable<ExecutionContext>("close") {
    override val help: String =
        """
        `close(+Stream, +Options)`
        
        Registered for ISO conformance only: the current implementation does not support close options such as `force(true)`, so every call raises a system error regardless of its arguments; use `close/1` instead.

        **Examples**

        ```prolog
        ?- close(user_output, [force(true)]).
        throws error(system_error, _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response = notSupported()
}
