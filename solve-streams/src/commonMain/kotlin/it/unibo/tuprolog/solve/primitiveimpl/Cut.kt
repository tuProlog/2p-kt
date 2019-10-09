package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl

/**
 * Implementation of primitive handling `'!'/0` behaviour
 *
 * @author Enrico
 */
internal object Cut : PrimitiveWrapper<ExecutionContextImpl>(Signature("!", 0)) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
            sequenceOf(
                    request.replySuccess(
                            request.context.substitution,
                            sideEffectManager = request.context.sideEffectManager.cut()
                    )
            )
}
