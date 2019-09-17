package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.exception.HaltException

/**
 * Implementation of primitive handling `halt/0` behaviour
 *
 * @author Enrico
 */
object Halt : PrimitiveWrapper(Signature("halt", 0)) {
    override val uncheckedImplementation: Primitive = {
        throw HaltException(context = it.context)
    }
}
