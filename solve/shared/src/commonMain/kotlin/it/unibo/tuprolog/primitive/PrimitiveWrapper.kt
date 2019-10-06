package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError

/**
 * Wrapper class for [Primitive] implementation
 *
 * @param signature Supported primitive signature
 *
 * @author Enrico
 * @author Giovanni
 */
abstract class PrimitiveWrapper<C : ExecutionContext>(val signature: Signature) {

    constructor(name: String, arity: Int, vararg: Boolean = false) : this(Signature(name, arity, vararg))

    /** A shorthand to get the primitive functor name */
    val functor: String = signature.name

    /** The field containing the implementation of the primitive, without any check for application to correct signature */
    protected abstract fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response>

    /** Checked primitive implementation */
    @Suppress("UNCHECKED_CAST")
    val primitive: Primitive by lazy { primitiveOf(signature, ::uncheckedImplementation as Primitive) }

    /** Gets this primitive description Pair formed by [signature] and [primitive] */
    val descriptionPair: Pair<Signature, Primitive> by lazy { signature to primitive }


    companion object {

        /** Utility function to ensure that all arguments of Solve.Request are instantiated and *not* (still) Variables */
        fun <C : ExecutionContext> Solve.Request<C>.ensuringAllArgumentsAreInstantiated(): Solve.Request<C> =
                arguments.withIndex().firstOrNull { it.value is Var }.let { notInstantiated ->
                    notInstantiated?.run {
                        throw InstantiationError(
                                context,
                                signature,
                                notInstantiated.index,
                                notInstantiated.value as Var
                        )
                    } ?: this
                }

        /** Utility function to ensure that all arguments of Solve.Request are [Numeric] */
        fun <C : ExecutionContext> Solve.Request<C>.ensuringAllArgumentsAreNumeric(): Solve.Request<C> =
                arguments.withIndex().firstOrNull { it.value !is Numeric }.let { notNumeric ->
                    notNumeric?.run {
                        throw TypeError(
                                context,
                                signature,
                                TypeError.Expected.NUMBER,
                                notNumeric.value,
                                notNumeric.index
                        )
                    } ?: this
                }
    }
}
