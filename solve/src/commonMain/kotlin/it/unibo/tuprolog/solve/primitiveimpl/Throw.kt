package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature

/**
 * Implementation of primitive handling `throw/1` behaviour
 *
 * @author Enrico
 */
object Throw : PrimitiveWrapper(Signature("throw", 1)) {

    override val uncheckedImplementation: Primitive = TODO()
}
