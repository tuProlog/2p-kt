package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.solver.StreamsExecutionContext

/**
 * Implementation of primitive handling `'!'/0` behaviour
 *
 * @author Enrico
 */
internal object Cut : PrimitiveWrapper<StreamsExecutionContext>("!", 0) {

    override fun uncheckedImplementation(request: Solve.Request<StreamsExecutionContext>): Sequence<Solve.Response> =
        with(request) {
            sequenceOf(
                replySuccess(
                    context.substitution,
                    sideEffectManager = context.sideEffectManager.cut()
                )
            )
        }
}
