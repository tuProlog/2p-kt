package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrCharCode
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.readCodeAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object GetCode1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("get_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrCharCode(0)
        return readCodeAndReply(currentInputChannel, first)
    }
}
