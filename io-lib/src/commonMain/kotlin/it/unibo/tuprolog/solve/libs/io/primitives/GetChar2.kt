package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readCharAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `get_char/2`: reads (consuming it) the next character from the input stream identified by the
 * first argument (an alias or `$stream(...)` term) and unifies it with the second, as `end_of_file` at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the first argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if it does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an output channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`in_character`) if the second argument is bound to
 * something other than `end_of_file` or a one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object GetChar2 : BinaryRelation.NonBacktrackable<ExecutionContext>("get_char") {
    override val help: String =
        """
        `get_char(+Stream, ?Char)`
        
        Reads and consumes the next character from the input stream `Stream` and unifies it with `Char`, which becomes `end_of_file` at the end of the stream. `Stream` must be an alias (e.g. `user_input`) or a `${'$'}stream(in, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an output stream a domain error (`stream_type`), where ISO prescribes a permission error. `Char` must be unbound, `end_of_file`, or a one-character atom, otherwise a type error (`in_character`) is raised. It fails, instead of raising an error, if the underlying channel has been closed.

        **Examples**

        ```prolog
        ?- get_char(user_input, ab).
        throws error(type_error(in_character, ab), _).

        ?- get_char(S, C).
        throws error(instantiation_error, _).

        ?- get_char(user_output, C).
        throws error(domain_error(stream_type, user_output), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        ensuringArgumentIsVarOrChar(1)
        return readCharAndReply(channel, second)
    }
}
