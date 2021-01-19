package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.ensuringArgumentIsInputChannel
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

object AtEndOfStream1 : UnaryPredicate.Predicative<ExecutionContext>("at_end_of_stream") {
    override fun Solve.Request<ExecutionContext>.compute(first: Term): Boolean =
        ensuringArgumentIsInputChannel(0).let { it.isClosed || it.isOver }
}
