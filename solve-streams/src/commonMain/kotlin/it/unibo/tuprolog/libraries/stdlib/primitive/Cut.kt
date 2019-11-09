package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl

/**
 * Implementation of primitive handling `'!'/0` behaviour
 *
 * @author Enrico
 */
internal object Cut : PrimitiveWrapper<ExecutionContextImpl>("!", 0) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> = with(request) {
        sequenceOf(
                replySuccess(
                        context.substitution,
                        sideEffectManager = context.sideEffectManager.cut()
                )
        )
    }
}
