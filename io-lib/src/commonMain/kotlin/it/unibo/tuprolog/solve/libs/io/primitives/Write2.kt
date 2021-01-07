package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object Write2 : BinaryRelation.NonBacktrackable<ExecutionContext>("write") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        val string = when (second) {
            is Atom -> second.value
            else -> second.toString()
        }
        return try {
            channel.write(string)
            replySuccess()
        } catch (_: IllegalStateException) {
            replyFail()
        }
    }
}
