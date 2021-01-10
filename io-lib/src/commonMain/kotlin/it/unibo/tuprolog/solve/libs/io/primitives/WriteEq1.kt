package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentOutputChannel
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.writeTermAndReply
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object WriteEq1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("writeq") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response =
        writeTermAndReply(currentOutputChannel, first, TermFormatter.readable())
}
