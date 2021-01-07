package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object PutChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("put_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsChar(0)
        return writeCharAndReply(currentOutputChannel, first as Atom)
    }

    internal fun Solve.Request<ExecutionContext>.writeCharAndReply(
        channel: OutputChannel<String>,
        arg: Atom
    ): Solve.Response {
        return try {
            channel.write(arg.value)
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
