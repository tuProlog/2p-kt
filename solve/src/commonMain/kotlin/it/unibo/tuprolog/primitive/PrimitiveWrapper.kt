package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError

/**
 * Wrapper class for [Primitive] implementation
 *
 * @author Enrico
 * @author Giovanni
 */
abstract class PrimitiveWrapper<C : ExecutionContext> : AbstractWrapper<Primitive> {

    constructor(signature: Signature) : super(signature)
    constructor(name: String, arity: Int, vararg: Boolean = false) : super(name, arity, vararg)

    /** The function expressing the implementation of the primitive, without any check for application to correct signature */
    protected abstract fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response>

    /** Checked primitive implementation */
    @Suppress("UNCHECKED_CAST")
    final override val wrappedImplementation: Primitive by lazy {
        primitiveOf(signature, ::uncheckedImplementation as Primitive)
    }


    companion object {

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Signature] and a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(signature: Signature, primitive: Primitive): PrimitiveWrapper<C> {
            return object : PrimitiveWrapper<C>(signature) {
                override fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response> {
                    return primitive(request)
                }
            }
        }

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(
            name: String,
            arity: Int,
            vararg: Boolean,
            primitive: Primitive
        ): PrimitiveWrapper<C> {
            return object : PrimitiveWrapper<C>(name, arity, vararg) {
                override fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response> {
                    return primitive(request)
                }
            }
        }

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(name: String, arity: Int, primitive: Primitive): PrimitiveWrapper<C> {
            return wrap(name, arity, false, primitive)
        }

        /** Utility function to ensure that all arguments of Solve.Request are instantiated and *not* (still) Variables */
        fun <C : ExecutionContext> Solve.Request<C>.ensuringAllArgumentsAreInstantiated(): Solve.Request<C> =
            arguments.withIndex().firstOrNull { it.value is Var }.let { notInstantiated ->
                notInstantiated?.run {
                    throw InstantiationError.forArgument(
                        context,
                        signature,
                        notInstantiated.index,
                        notInstantiated.value as Var
                    )
                } ?: this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentsIsNumeric(index: Int): Solve.Request<C> {
            return when (val arg = arguments[index]) {
                !is Numeric -> throw TypeError(
                    "Argument $index of ${this.signature}` should be a ${TypeError.Expected.NUMBER}",
                    context = context,
                    expectedType = TypeError.Expected.NUMBER,
                    actualValue = arg
                )
                else -> this
            }
        }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentsIsInteger(index: Int): Solve.Request<C> {
            return when (val arg = arguments[index]) {
                !is Integer -> throw TypeError(
                    "Argument $index of ${this.signature}` should be a ${TypeError.Expected.INTEGER}",
                    context = context,
                    expectedType = TypeError.Expected.INTEGER,
                    actualValue = arg
                )
                else -> this
            }
        }
    }
}
