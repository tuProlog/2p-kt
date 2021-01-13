package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object Close1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("close") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val channel = ensuringArgumentIsChannel(0)
        try {
            channel.close()
            return when (channel) {
                is InputChannel<*> -> {
                    replySuccess {
                        closeInputChannels(context.inputChannels.aliasesOf(channel as InputChannel<String>))
                    }
                }
                is OutputChannel<*> -> {
                    replySuccess {
                        closeOutputChannels(context.outputChannels.aliasesOf(channel as OutputChannel<String>))
                    }
                }
                else -> throw IllegalStateException("This should never happen")
            }
        } catch (_: IllegalStateException) {
            return replyFail()
        }
    }
}
