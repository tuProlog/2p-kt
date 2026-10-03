package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeTermAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Implements ISO's `writeq/1`: formats the argument quoting atoms/functors where needed to remain re-readable
 * (i.e. [TermFormatter.readable]) and writes it to the current output stream (see
 * [IOPrimitiveUtils.currentOutputChannel]).
 *
 * Fails, rather than erroring, if the channel is closed.
 */
object WriteEq1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("writeq") {
    override val help: String =
        """
        `writeq(@Term)`
        
        Writes `Term` to the current output stream (standard output, unless changed via `set_output/1`) so that it can be read back: atoms and functors are quoted where needed, operators are written in operator notation, and `'${'$'}VAR'(N)` terms are written as variable letters. It succeeds deterministically. The current implementation formats operators according to the default operator table rather than the current one. It fails, instead of raising an error, if the underlying channel has been closed.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response =
        writeTermAndReply(currentOutputChannel, first, TermFormatter.readable())
}
