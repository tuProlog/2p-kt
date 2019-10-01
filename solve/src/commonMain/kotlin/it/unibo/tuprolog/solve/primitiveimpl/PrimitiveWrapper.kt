package it.unibo.tuprolog.solve.primitiveimpl

import it.unibo.tuprolog.primitive.Primitive
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.primitiveOf
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/**
 * Wrapper class for [Primitive] implementation
 *
 * @param signature Supported primitive signature
 *
 * @author Enrico
 */
abstract class PrimitiveWrapper<C : ExecutionContext>(val signature: Signature) {

    /** A shorthand to get the primitive functor name */
    val functor: String = signature.name

    /** The field containing the implementation of the primitive, without any check for application to correct signature */
    protected abstract val uncheckedImplementation: (Solve.Request<C>) -> Sequence<Solve.Response>

    /** Checked primitive implementation */
    @Suppress("UNCHECKED_CAST")
    val primitive: Primitive by lazy { primitiveOf(signature, uncheckedImplementation as Primitive) }

    /** Gets this primitive description Pair formed by [signature] and [primitive] */
    val descriptionPair: Pair<Signature, Primitive> by lazy { signature to primitive }
}
