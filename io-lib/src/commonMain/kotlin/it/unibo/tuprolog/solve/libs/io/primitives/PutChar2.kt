package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeCharAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `put_char/2`: writes the one-character atom second argument to the output stream identified by
 * the first (an alias or `$stream(...)` term).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if either argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.ExistenceError if the first argument does not identify an open channel.
 * @throws it.unibo.tuprolog.solve.exception.error.DomainError (`stream_type`) if it identifies an input channel instead.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`character`) if the second argument is bound but not a
 * one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PutChar2 : BinaryRelation.NonBacktrackable<ExecutionContext>("put_char") {
    override val help: String =
        """
        `put_char(+Stream, +Char)`
        
        Writes the character `Char` to the output stream `Stream`, succeeding deterministically. `Stream` must be an alias (e.g. `user_output`) or a `${'$'}stream(out, Id)` term denoting an open stream: an unbound `Stream` raises an instantiation error, an unknown one an existence error, and an input stream a domain error (`stream_type`), where ISO prescribes a permission error. `Char` must be a one-character atom, otherwise (even when unbound) a type error (`character`) is raised. It fails, instead of raising an error, if the underlying channel has been closed.

        **Examples**

        ```prolog
        ?- put_char(user_output, a).
        % prints: a
        yes.

        ?- put_char(user_output, ab).
        throws error(type_error(character, ab), _).

        ?- put_char(user_input, a).
        throws error(domain_error(stream_type, user_input), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        ensuringArgumentIsChar(1)
        return writeCharAndReply(channel, second as Atom)
    }
}
