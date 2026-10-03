package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.peekCharAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `peek_char/1`: unifies the argument with the next character of the current input stream (see
 * [IOPrimitiveUtils.currentInputChannel]) *without* consuming it (so a subsequent [GetChar1]/[Read1] sees the same
 * character again), as `end_of_file` at end of stream.
 *
 * @throws it.unibo.tuprolog.solve.exception.error.TypeError (`in_character`) if the argument is bound to something
 * other than `end_of_file` or a one-character atom.
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object PeekChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("peek_char") {
    override val help: String =
        """
        `peek_char(?Char)`
        
        Unifies `Char` with the next character of the current input stream (standard input, unless changed via `set_input/1`) without consuming it, or with `end_of_file` at the end of the stream. It is deterministic. `Char` must be unbound, `end_of_file`, or a one-character atom, otherwise a type error (`in_character`) is raised. It fails, instead of raising an error, if the underlying channel has been closed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrChar(0)
        return peekCharAndReply(currentInputChannel, first)
    }
}
