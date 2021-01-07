package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object SetInput : UnaryPredicate.NonBacktrackable<ExecutionContext>("set_input") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsInputChannel(0)
        return replySuccess {
            resetInputChannels(context.inputChannels.setCurrent(channel))
        }
    }
}
