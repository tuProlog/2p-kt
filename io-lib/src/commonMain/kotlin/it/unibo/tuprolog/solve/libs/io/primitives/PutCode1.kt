package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object PutCode1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("put_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsCharCode(0)
        return writeCodeAndReply(currentOutputChannel, first as Integer)
    }

    internal fun Solve.Request<ExecutionContext>.writeCodeAndReply(
        channel: OutputChannel<String>,
        arg: Integer
    ): Solve.Response {
        return try {
            channel.write("${arg.intValue.toChar()}")
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
