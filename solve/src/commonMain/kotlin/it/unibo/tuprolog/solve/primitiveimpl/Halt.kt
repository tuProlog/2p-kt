package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl

/**
 * Implementation of primitive handling `halt/0` behaviour
 *
 * @author Enrico
 */
object Halt : PrimitiveWrapper(Signature("halt", 0)) {
    override val uncheckedImplementation: Primitive = {
        // TODO: 25/09/2019 remove that
        it as Solve.Request<ExecutionContextImpl>

        throw HaltException(context = it.context)
    }
}
