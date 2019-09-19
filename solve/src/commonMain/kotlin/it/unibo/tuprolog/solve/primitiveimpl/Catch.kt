package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature

/**
 * Implementation of primitive handling `catch/3` behaviour
 *
 * @author Enrico
 */
object Catch : PrimitiveWrapper(Signature("catch", 3)) {

    override val uncheckedImplementation: Primitive = {
        TODO()
    }
}
