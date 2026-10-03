package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.peekCharAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `peek_char/2`: unifies the second argument with the next character of the input stream
 * identified by the first (an alias or `$stream(...)` term) *without* consuming it, as `end_of_file` at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`in_character`) if the second argument is bound to
 * something other than `end_of_file` or a one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PeekChar2 : BinaryRelation.NonBacktrackable<ExecutionContext>("peek_char") {
    override val help: String =
        """
        `peek_char(+Stream, ?Char)`
        
        Unifies `Char` with the next character of the input stream `Stream` without consuming it, or with `end_of_file` at the end of the stream. `Stream` must be an alias (e.g. `user_input`) or a `${'$'}stream(in, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an output stream a domain error (`stream_type`), where ISO prescribes a permission error. `Char` must be unbound, `end_of_file`, or a one-character atom, otherwise a type error (`in_character`) is raised. It fails, instead of raising an error, if the underlying channel has been closed.

        **Examples**

        ```prolog
        ?- peek_char(user_input, ab).
        throws error(type_error(in_character, ab), _).

        ?- peek_char(S, C).
        throws error(instantiation_error, _).

        ?- peek_char(user_output, C).
        throws error(domain_error(stream_type, user_output), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        ensuringArgumentIsVarOrChar(1)
        return peekCharAndReply(channel, second)
    }
}
