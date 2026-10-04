package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrCharCode
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.peekCodeAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `peek_code/2`: unifies the second argument with the code of the next character of the input
 * stream identified by the first (an alias or `$stream(...)` term) *without* consuming it, as `-1` at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PeekCode2 : BinaryRelation.NonBacktrackable<ExecutionContext>("peek_code") {
    override val help: String =
        """
        `peek_code(+Stream, ?Code)`
        
        Unifies `Code` with the code of the next character of the input stream `Stream` without consuming it, or with `-1` at the end of the stream. `Stream` must be an alias (e.g. `user_input`) or a `${'$'}stream(in, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an output stream a domain error (`stream_type`), where ISO prescribes a permission error. `Code` must be unbound or an integer (type error otherwise) within the character-code range or `-1` (representation error otherwise). It fails, instead of raising an error, if the underlying channel has been closed.

        **Examples**

        ```prolog
        ?- peek_code(user_input, a).
        throws error(type_error(integer, a), _).

        ?- peek_code(S, C).
        throws error(instantiation_error, _).

        ?- peek_code(user_output, C).
        throws error(domain_error(stream_type, user_output), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        ensuringArgumentIsVarOrCharCode(1)
        return peekCodeAndReply(channel, second)
    }
}
