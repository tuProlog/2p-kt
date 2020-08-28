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
 * @param contexts a stack of contexts localising the exception
 * @param type The error type structure
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Giovanni
 * @author Enrico
 */
abstract class PrologError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("type") open val type: Struct,
    @JsName("extraData") open val extraData: Term? = null
) : TuPrologRuntimeException(message, cause, contexts) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        type: Struct,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), type, extraData)

    constructor(cause: Throwable?, context: ExecutionContext, type: Struct, extraData: Term? = null)
            : this(cause?.toString(), cause, context, type, extraData)

    /** The error Struct as described in Prolog standard: `error(error_type, error_extra)` */
    val errorStruct: Struct by lazy { generateErrorStruct() }

    private fun generateErrorStruct() =
        extraData?.let { errorStructOf(type, it) } ?: errorStructOf(type)

    abstract override fun updateContext(newContext: ExecutionContext): PrologError

    abstract override fun pushContext(newContext: ExecutionContext): PrologError

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
        ): PrologError = of(message, cause, arrayOf(context), type, extraData)

        @JvmStatic
        @JsName("ofManyContexts")
        fun of(
            message: String? = null,
            cause: Throwable? = null,
            contexts: Array<ExecutionContext>,
            type: Struct,
            extraData: Term? = null
        ): PrologError = with(type) {
            when {
                functor == InstantiationError.typeFunctor -> InstantiationError(message, cause, contexts, extraData)
                functor == SystemError.typeFunctor -> SystemError(message, cause, contexts, extraData)
                functor == ExistenceError.typeFunctor && type.arity == 2 ->
                    ExistenceError(message, cause, contexts, ExistenceError.ObjectType.fromTerm(type[0])!!, type[1])
                functor == DomainError.typeFunctor && arity == 2 && DomainError.Expected.fromTerm(args.first()) != null ->
                    DomainError(
                        message,
                        cause,
                        contexts,
                        DomainError.Expected.fromTerm(args.first())!!,
                        args[1],
                        extraData
                    )
                functor == TypeError.typeFunctor && arity == 2 && TypeError.Expected.fromTerm(args.first()) != null ->
                    TypeError(message, cause, contexts, TypeError.Expected.fromTerm(args.first())!!, args[1], extraData)
                functor == EvaluationError.typeFunctor && arity == 1 && EvaluationError.Type.fromTerm(args.single()) != null ->
                    EvaluationError(message, cause, contexts, EvaluationError.Type.fromTerm(args.single())!!, extraData)
                functor == MessageError.typeFunctor -> MessageError(message, cause, contexts, extraData)
                else -> object : PrologError(message, cause, contexts, type, extraData) {
                    override fun updateContext(newContext: ExecutionContext): PrologError =
                        of(this.message, this.cause, this.contexts.setFirst(newContext), this.type, this.extraData)

                    override fun pushContext(newContext: ExecutionContext): PrologError =
                        of(this.message, this.cause, this.contexts.addLast(newContext), this.type, this.extraData)
                }
            }
        }
    }
}