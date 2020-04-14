package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.*
import it.unibo.tuprolog.solve.exception.error.ErrorUtils.errorStructOf
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

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
    @JsName("type") open val type: Struct,
    @JsName("extraData") open val extraData: Term? = null
) : TuPrologRuntimeException(message, cause, context) {

    constructor(cause: Throwable?, context: ExecutionContext, type: Struct, extraData: Term? = null)
            : this(cause?.toString(), cause, context, type, extraData)

    /** The error Struct as described in Prolog standard: `error(error_type, error_extra)` */
    val errorStruct: Struct by lazy { generateErrorStruct() }

    private fun generateErrorStruct() =
        extraData?.let { errorStructOf(type, it) } ?: errorStructOf(type)

    override fun updateContext(newContext: ExecutionContext): PrologError =
        of(message, cause, newContext, type, extraData)

    override fun toString(): String = errorStruct.toString()

    companion object {

        /**
         * Factory method for [PrologError]s
         *
         * It creates correct subclass instance if [type] detected, otherwise defaulting to a [PrologError] instance
         */
        @JvmStatic
        @JsName("of")
        fun of(
            message: String? = null,
            cause: Throwable? = null,
            context: ExecutionContext,
            type: Struct,
            extraData: Term? = null
        ): PrologError = with(type) {
            when {
                functor == InstantiationError.typeFunctor -> InstantiationError(message, cause, context, extraData)
                functor == SystemError.typeFunctor -> SystemError(message, cause, context, extraData)
                functor == TypeError.typeFunctor && arity == 2 && TypeError.Expected.fromTerm(args.first()) != null ->
                    TypeError(message, cause, context, TypeError.Expected.fromTerm(args.first())!!, args[1], extraData)
                functor == EvaluationError.typeFunctor && arity == 1 && EvaluationError.Type.fromTerm(args.single()) != null ->
                    EvaluationError(message, cause, context, EvaluationError.Type.fromTerm(args.single())!!, extraData)
                functor == MessageError.typeFunctor -> MessageError(message, cause, context, extraData)
                else -> object : PrologError(message, cause, context, type, extraData) {}
            }
        }
    }
}