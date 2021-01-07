package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrChar
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

object GetChar1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("get_char") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrChar(0)
        return readCharAndReply(currentInputChannel, first)
    }

    internal fun Solve.Request<ExecutionContext>.readCharAndReply(channel: InputChannel<String>, arg: Term): Solve.Response {
        return try {
            val char = channel.read() ?: "end_of_file"
            replyWith(arg mguWith Atom.of(char))
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
