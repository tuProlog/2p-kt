package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl

/**
 * Implementation of primitive handling `'!'/0` behaviour
 *
 * @author Enrico
 */
internal object Cut : PrimitiveWrapper<ExecutionContextImpl>(Signature("!", 0)) {

    override val uncheckedImplementation: (Solve.Request<ExecutionContextImpl>) -> Sequence<Solve.Response> = {
        sequenceOf(
                it.replySuccess(
                        it.context.substitution,
                        sideEffectManager = it.context.sideEffectManager.cut()
                )
        )
    }
}