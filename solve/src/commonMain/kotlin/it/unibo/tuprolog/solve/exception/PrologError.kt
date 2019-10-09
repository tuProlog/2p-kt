package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils.errorStructOf
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError

/**
 * Base class for Standard Prolog Errors and possibly other custom Primitive errors
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param type The error type structure
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Giovanni
 * @author Enrico
 */
abstract class PrologError(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        open val type: Struct,
        open val extraData: Term? = null
) : TuPrologRuntimeException(message, cause, context) {

    /** The error Struct as described in Prolog standard: `error(error_type, error_extra)` */
    val errorStruct: Struct by lazy {
        extraData?.let { errorStructOf(type, it) }
                ?: errorStructOf(type)
    }

    constructor(cause: Throwable?, context: ExecutionContext, type: Struct, extraData: Term? = null)
            : this(cause?.toString(), cause, context, type, extraData)

    override fun toString(): String = errorStruct.toString()

    override fun updateContext(context: ExecutionContext): PrologError {
        // TODO @Enrico which one do you prefer?
//        throw NotImplementedError("Subclasses of PrologError should override this method")
        return object : PrologError(message, cause, context, type, extraData) { }
    }

    companion object {

        /**
         * Factory method for [PrologError]s
         *
         * It creates correct subclass instance if [type] detected, otherwise defaulting to a [PrologError] instance
         */
        fun of(
                message: String? = null,
                cause: Throwable? = null,
                context: ExecutionContext,
                type: Struct,
                extraData: Term? = null
        ) = with(type) {
            when {
                functor == InstantiationError.typeFunctor -> InstantiationError(message, cause, context, extraData)
                functor == SystemError.typeFunctor -> SystemError(message, cause, context, extraData)
                functor == TypeError.typeFunctor && arity == 2 && TypeError.Expected.fromTerm(args.first()) != null ->
                    TypeError(message, cause, context, TypeError.Expected.fromTerm(args.first())!!, args[1], extraData)
                else -> object : PrologError(message, cause, context, type, extraData) {}
            }
        }
    }
}