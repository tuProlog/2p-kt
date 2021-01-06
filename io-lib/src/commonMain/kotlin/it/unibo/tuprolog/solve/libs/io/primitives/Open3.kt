package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.libs.io.IOMode
import it.unibo.tuprolog.solve.libs.io.openInputChannel
import it.unibo.tuprolog.solve.libs.io.openOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsIOMode
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsUrl
import it.unibo.tuprolog.solve.primitive.Solve.Request
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object Open3 : TernaryRelation.NonBacktrackable<ExecutionContext>("open") {
    override fun Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Response {
        val url = ensuringArgumentIsUrl(0)
        val mode = ensuringArgumentIsIOMode(1)
        ensuringArgumentIsVariable(2)
        return when (mode) {
            IOMode.READ -> replyOpeningStream(url.openInputChannel(), third)
            IOMode.WRITE -> replyOpeningStream(url.openOutputChannel(false), third)
            IOMode.APPEND -> replyOpeningStream(url.openOutputChannel(true), third)
        }
    }

    private fun Request<ExecutionContext>.replyOpeningStream(channel: OutputChannel<String>, third: Term): Response {
        val streamTerm = channel.streamTerm
        return replyWith(third mguWith streamTerm) {
            openOutputChannel("output_channel${channel.streamTerm[1]}", channel)
        }
    }

    private fun Request<ExecutionContext>.replyOpeningStream(channel: InputChannel<String>, third: Term): Response {
        val streamTerm = channel.streamTerm
        return replyWith(third mguWith streamTerm) {
            openInputChannel("input_channel${channel.streamTerm[1]}", channel)
        }
    }
}
