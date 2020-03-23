package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.solve.AbstractWrapper
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
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
abstract class PrimitiveWrapper<C : ExecutionContext> :
    AbstractWrapper<Primitive> {

    constructor(signature: Signature) : super(signature)
    constructor(name: String, arity: Int, vararg: Boolean = false) : super(name, arity, vararg)

    /** The function expressing the implementation of the primitive, without any check for application to correct signature */
    protected abstract fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response>

    /** Checked primitive implementation */
    @Suppress("UNCHECKED_CAST")
    final override val wrappedImplementation: Primitive =
        primitiveOf(signature, ::uncheckedImplementation as Primitive)

    companion object {

        /** Private class to support the wrap methods, without using the object literal notation */
        private class FromFunction<C : ExecutionContext>(signature: Signature, private val uncheckedPrimitive: Primitive)
            : PrimitiveWrapper<C>(signature) {

            constructor(name: String, arity: Int, vararg: Boolean = false, uncheckedPrimitive: Primitive)
                    : this(Signature(name, arity, vararg), uncheckedPrimitive)

            override fun uncheckedImplementation(request: Solve.Request<C>): Sequence<Solve.Response> =
                uncheckedPrimitive(request)
        }

        // TODO: 16/01/2020 test the three "wrap" functions

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Signature] and a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(signature: Signature, primitive: Primitive): PrimitiveWrapper<C> =
            FromFunction(signature, primitive)

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(
            name: String,
            arity: Int,
            vararg: Boolean,
            primitive: Primitive
        ): PrimitiveWrapper<C> =
            FromFunction(name, arity, vararg, primitive)

        /**
         * Utility factory to build a [PrimitiveWrapper] out of a [Primitive] function
         */
        fun <C : ExecutionContext> wrap(name: String, arity: Int, primitive: Primitive): PrimitiveWrapper<C> =
            wrap(name, arity, false, primitive)

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

        // TODO: 16/01/2020 test those below ensure methods

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsNumeric(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Numeric -> throw TypeError(
                    "Argument $index of ${this.signature}` should be a ${TypeError.Expected.NUMBER}",
                    context = context,
                    expectedType = TypeError.Expected.NUMBER,
                    actualValue = arg
                )
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsStruct(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
                !is Struct -> throw TypeError(
                    "Argument $index of ${this.signature}` should be a ${TypeError.Expected.COMPOUND}",
                    context = context,
                    expectedType = TypeError.Expected.COMPOUND,
                    actualValue = arg
                )
                else -> this
            }

        fun <C : ExecutionContext> Solve.Request<C>.ensuringArgumentIsInteger(index: Int): Solve.Request<C> =
            when (val arg = arguments[index]) {
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
