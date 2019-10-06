package it.unibo.tuprolog.primitive

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
    protected abstract fun uncheckedImplementation(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response>

    /** Gets this primitive description Pair formed by [signature] and [primitive] */
    val descriptionPair: Pair<Signature, Primitive> by lazy { signature to primitive }

    companion object {

        fun ensuringAllArgumentsAreInstantiated(req: Solve.Request<ExecutionContext>, f: (Solve.Request<ExecutionContext>) -> Solve.Response): Solve.Response {
            val nonInstantiated = req.arguments.withIndex().firstOrNull { it.value is Var }

            return if (nonInstantiated !== null) {
                req.replyException(InstantiationError(
                        req.context,
                        req.signature,
                        nonInstantiated.index,
                        nonInstantiated.value as Var
                ))
            } else {
                f(req)
            }
        }

        fun ensuringAllArgumentsAreStructs(req: Solve.Request<ExecutionContext>, f: (Solve.Request<ExecutionContext>) -> Solve.Response): Solve.Response {
            val nonNumeric = req.arguments.withIndex().firstOrNull { it.value !is Struct }

            return if (nonNumeric !== null) {
                req.replyException(TypeError(
                        req.context,
                        req.signature,
                        TypeError.Expected.COMPOUND,
                        nonNumeric.value,
                        nonNumeric.index
                ))
            } else {
                f(req)
            }
        }

        fun ensuringAllArgumentsAreNumeric(req: Solve.Request<ExecutionContext>, f: (Solve.Request<ExecutionContext>) -> Solve.Response): Solve.Response {
            val nonNumeric = req.arguments.withIndex().firstOrNull { it.value !is Numeric }

            return if (nonNumeric !== null) {
                req.replyException(TypeError(
                        req.context,
                        req.signature,
                        TypeError.Expected.NUMBER,
                        nonNumeric.value,
                        nonNumeric.index
                ))
            } else {
                f(req)
            }
        }

    }
}
