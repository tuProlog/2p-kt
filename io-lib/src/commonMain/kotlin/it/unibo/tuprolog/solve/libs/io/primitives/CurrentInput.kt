package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrStream
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object CurrentInput : UnaryPredicate.NonBacktrackable<ExecutionContext>("current_input") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsVarOrStream(0)
        val currentChannel = context.inputChannels.let { it.current ?: it.stdIn }
        return when (channel) {
            null -> replyWith(mgu(first, currentChannel.streamTerm))
            currentChannel -> replySuccess()
            else -> replyFail()
        }
    }
}
