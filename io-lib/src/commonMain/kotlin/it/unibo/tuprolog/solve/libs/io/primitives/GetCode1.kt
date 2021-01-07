package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrCharCode
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object GetCode1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("get_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrCharCode(0)
        return readAndReply(context.inputChannels.let { it.current ?: it.stdIn }, first)
    }

    internal fun Solve.Request<ExecutionContext>.readAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        return try {
            val code = channel.read()?.get(0)?.toInt() ?: -1
            replyWith(arg mguWith Integer.of(code))
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
