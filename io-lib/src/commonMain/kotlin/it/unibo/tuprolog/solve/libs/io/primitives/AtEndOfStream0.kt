package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.currentInputChannel
import it.unibo.tuprolog.solve.primitive.PredicateWithoutArguments
import it.unibo.tuprolog.solve.primitive.Solve

object AtEndOfStream0 : PredicateWithoutArguments.Predicative<ExecutionContext>("at_end_of_stream") {
    override fun Solve.Request<ExecutionContext>.compute(): Boolean =
        currentInputChannel.let { it.isClosed || it.isOver }
}
