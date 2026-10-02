package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.PredicateWithoutArguments
import it.unibo.tuprolog.solve.primitive.Solve

object NewLine : PredicateWithoutArguments.NonBacktrackable<ExecutionContext>("nl") {
    override val help: String =
        """
        `nl`
        
        Writes a newline to the current output channel. Fails if no current output channel is available.
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(): Solve.Response =
        context.outputChannels.current.let {
            if (it == null) {
                replyFail()
            } else {
                it.write("\n")
                replySuccess()
            }
        }
}
