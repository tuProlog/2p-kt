package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeCharAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `put_char/1`: writes the one-character atom argument to the current output stream (see
 * [IOPrimitiveUtils.currentOutputChannel]).
 *
 * @throws it.unibo.tuprolog.solve.exception.error.InstantiationError if the argument is unbound.
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`character`) if it is bound but not a one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PutChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("put_char") {
    override val help: String =
        """
        `put_char(+Char)`
        
        Writes the character `Char` to the current output stream (standard output, unless changed via `set_output/1`), succeeding deterministically. `Char` must be a one-character atom, otherwise a type error (`character`) is raised; the current implementation raises that type error even when `Char` is unbound, where ISO prescribes an instantiation error. It fails, instead of raising an error, if the underlying channel has been closed.

        **Examples**

        ```prolog
        ?- put_char(a).
        % prints: a
        yes.

        ?- put_char(ab).
        throws error(type_error(character, ab), _).

        ?- put_char(C).
        throws error(type_error(character, _), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsChar(0)
        return writeCharAndReply(currentOutputChannel, first as Atom)
    }
}
