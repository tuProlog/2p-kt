package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsVarOrCharCode
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.peekCodeAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object PeekCode1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("peek_code") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsVarOrCharCode(0)
        return peekCodeAndReply(currentInputChannel, first)
    }
}
