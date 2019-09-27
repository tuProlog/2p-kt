package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl

/**
 * Implementation of primitive handling `'!'/0` behaviour
 *
 * @author Enrico
 */
object Cut : PrimitiveWrapper(Signature("!", 0)) {

    override val uncheckedImplementation: Primitive = {
        // TODO: 25/09/2019 remove that
        it as Solve.Request<ExecutionContextImpl>

        sequenceOf(
                it.replySuccess(
                        it.context.substitution,
                        sideEffectManager = it.context.sideEffectManager.cut()
                )
        )
    }
}