package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeCodeAndReply
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object PutCode2 : BinaryRelation.NonBacktrackable<ExecutionContext>("put_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
    ): Solve.Response {
        val channel = ensuringArgumentIsOutputChannel(0)
        ensuringArgumentIsCharCode(1)
        return writeCodeAndReply(channel, first as Integer)
    }
}
