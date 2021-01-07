package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object PeekChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("peek_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrChar(0)
        return peekCharAndReply(context.inputChannels.let { it.current ?: it.stdIn }, first)
    }

    internal fun Solve.Request<ExecutionContext>.peekCharAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        return try {
            val char = channel.peek() ?: "end_of_file"
            replyWith(arg mguWith Atom.of(char))
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
