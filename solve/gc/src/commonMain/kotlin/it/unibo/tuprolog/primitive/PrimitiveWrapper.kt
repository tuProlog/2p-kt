package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError

/**
 * Wrapper class for [Primitive] implementation
 *
 * @author Enrico
 * @author Giovanni
 */
abstract class PrimitiveWrapper(
        /** Supported primitive signature */
        val signature: Signature
) {

    constructor(name: String, arity: Int, vararg: Boolean = false) : this(Signature(name, arity, vararg))

    /** A shorthand to get the primitive functor name */
    val functor: String = signature.name

    /** Checked primitive implementation */
    val primitive: Primitive by lazy { primitiveOf(signature, this::uncheckedImplementation) }

    /** The template method represents the implementation of the primitive, without any check for application to correct signature */
    protected abstract fun uncheckedImplementation(request: Solve.Request): Sequence<Solve.Response>

    /** Gets this primitive description Pair formed by [signature] and [primitive] */
    val descriptionPair: Pair<Signature, Primitive> by lazy { signature to primitive }

    companion object {

        fun ensuringAllArgumentsAreInstantiated(req: Solve.Request, f: (Solve.Request) -> Solve.Response): Solve.Response {
            val nonInstantiated = req.arguments.withIndex().firstOrNull { it.value is Var }

            return if (nonInstantiated !== null) {
                req.toExceptionalResponse(InstantiationError(
                        nonInstantiated.index,
                        nonInstantiated.value as Var,
                        req.signature
                ))
            } else {
                f(req)
            }
        }

        fun ensuringAllArgumentsAreStructs(req: Solve.Request, f: (Solve.Request) -> Solve.Response): Solve.Response {
            val nonNumeric = req.arguments.withIndex().firstOrNull { it.value !is Struct }

            return if (nonNumeric !== null) {
                req.toExceptionalResponse(TypeError(
                        nonNumeric.index,
                        TypeError.Expected.COMPOUND,
                        nonNumeric.value,
                        req.signature
                ))
            } else {
                f(req)
            }
        }

        fun ensuringAllArgumentsAreNumeric(req: Solve.Request, f: (Solve.Request) -> Solve.Response): Solve.Response {
            val nonNumeric = req.arguments.withIndex().firstOrNull { it.value !is Numeric }

            return if (nonNumeric !== null) {
                req.toExceptionalResponse(TypeError(
                        nonNumeric.index,
                        TypeError.Expected.NUMBER,
                        nonNumeric.value,
                        req.signature
                ))
            } else {
                f(req)
            }
        }

    }
}
