package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.primitive.PredicateWithoutArguments
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implements ISO's `at_end_of_stream/0`: succeeds iff the current input stream (see
 * [IOPrimitiveUtils.currentInputChannel]) is closed or exhausted.
 *
 * ```prolog
 * ?- at_end_of_stream.
 * ```
 */
object AtEndOfStream0 : PredicateWithoutArguments.Predicative<ExecutionContext>("at_end_of_stream") {
    override val help: String =
        """
        `at_end_of_stream`
        
        Succeeds iff the current input stream (standard input, unless changed via `set_input/1`) is closed or has no more characters to read, and fails otherwise. It is deterministic and raises no errors.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(): Boolean =
        currentInputChannel.let { it.isClosed || it.isOver }
}
