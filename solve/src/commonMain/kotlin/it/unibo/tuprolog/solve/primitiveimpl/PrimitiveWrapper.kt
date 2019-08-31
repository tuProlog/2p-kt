package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.primitiveOf

/**
 * Wrapper class for [Primitive] implementation
 *
 * @author Enrico
 */
abstract class PrimitiveWrapper(
        /** Supported primitive signature */
        val signature: Signature
) {

    /** Checked primitive implementation */
    val primitive: Primitive by lazy { primitiveOf(signature, uncheckedImplementation) }

    /** The field containing the implementation of the primitive, without any check for application to correct signature */
    protected abstract val uncheckedImplementation: Primitive
}
