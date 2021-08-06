package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.format
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ErrorUtils.errorStructOf
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.MessageError
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.exception.error.PermissionError.Operation
import it.unibo.tuprolog.solve.exception.error.PermissionError.Permission
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.exception.error.RepresentationError.Limit
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * Base class for errors which may occur during resolution, possibly because of [Primitive]s execution
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
abstract class LogicError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("type") open val type: Struct,
    @JsName("extraData") open val extraData: Term? = null
) : ResolutionException(message, cause, contexts) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        type: Struct,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), type, extraData)

    constructor(cause: Throwable?, context: ExecutionContext, type: Struct, extraData: Term? = null) :
        this(cause?.toString(), cause, context, type, extraData)

    /** The error Struct as described in Prolog standard: `error(error_type, error_extra)` */
    @JsName("errorStruct")
    val errorStruct: Struct by lazy { generateErrorStruct() }

    private fun generateErrorStruct() =
        extraData?.let { errorStructOf(type, it) } ?: errorStructOf(type)

    abstract override fun updateContext(newContext: ExecutionContext, index: Int): LogicError

    abstract override fun updateLastContext(newContext: ExecutionContext): LogicError

    abstract override fun pushContext(newContext: ExecutionContext): LogicError

    override fun toString(): String = errorStruct.toString()

    companion object {

        internal fun <E : LogicError> message(message: String, f: (String, Atom) -> E): E =
            f(message, Atom.of(message))

        internal fun Term.pretty(): String =
            format(TermFormatter.prettyVariables())

        internal fun Signature.pretty(): String =
            toIndicator().toString()

        /**
         * Factory method for [LogicError]s
         *
         * It creates correct subclass instance if [type] detected, otherwise defaulting to a [LogicError] instance
         */
        @JvmStatic
        @JsName("of")
        fun of(
            message: String? = null,
            cause: Throwable? = null,
            context: ExecutionContext,
            type: Struct,
            extraData: Term? = null
        ): LogicError = of(message, cause, arrayOf(context), type, extraData)

        private fun customError(
            message: String? = null,
            cause: Throwable? = null,
            contexts: Array<ExecutionContext>,
            type: Struct,
            extraData: Term? = null
        ): LogicError = object : LogicError(message ?: type.pretty(), cause, contexts, type, extraData) {
            override fun updateContext(newContext: ExecutionContext, index: Int): LogicError =
                of(this.message, this.cause, this.contexts.setItem(index, newContext), this.type, this.extraData)

            override fun updateLastContext(newContext: ExecutionContext): LogicError =
                updateContext(newContext, this.contexts.lastIndex)

            override fun pushContext(newContext: ExecutionContext): LogicError =
                of(this.message, this.cause, this.contexts.addLast(newContext), this.type, this.extraData)
        }

        @JvmStatic
        @JsName("ofManyContexts")
        fun of(
            message: String? = null,
            cause: Throwable? = null,
            contexts: Array<ExecutionContext>,
            type: Struct,
            extraData: Term? = null
        ): LogicError = with(type) {
            val actualMessage = message ?: type.pretty()
            try {
                when {
                    functor == InstantiationError.typeFunctor ->
                        InstantiationError(actualMessage, cause, contexts, Var.anonymous(), extraData)
                    functor == SystemError.typeFunctor ->
                        SystemError(actualMessage, cause, contexts, extraData)
                    functor == SyntaxError.typeFunctor ->
                        SyntaxError(actualMessage, cause, contexts, extraData)
                    functor == MessageError.typeFunctor ->
                        MessageError(actualMessage, cause, contexts, extraData)
                    functor == RepresentationError.typeFunctor && arity == 1 ->
                        RepresentationError(actualMessage, cause, contexts, Limit.fromTerm(type[0])!!, extraData)
                    functor == ExistenceError.typeFunctor && arity == 2 ->
                        ExistenceError(
                            actualMessage,
                            cause,
                            contexts,
                            ExistenceError.ObjectType.fromTerm(type[0])!!,
                            type[1]
                        )
                    functor == DomainError.typeFunctor && arity == 2 ->
                        DomainError(
                            actualMessage,
                            cause,
                            contexts,
                            DomainError.Expected.fromTerm(getArgAt(0))!!,
                            getArgAt(1),
                            extraData
                        )
                    functor == TypeError.typeFunctor && arity == 2 ->
                        TypeError(
                            actualMessage,
                            cause,
                            contexts,
                            TypeError.Expected.fromTerm(getArgAt(0))!!,
                            getArgAt(1),
                            extraData
                        )
                    functor == EvaluationError.typeFunctor && arity == 1 ->
                        EvaluationError(
                            actualMessage,
                            cause,
                            contexts,
                            EvaluationError.Type.fromTerm(getArgAt(0))!!,
                            extraData
                        )
                    functor == PermissionError.typeFunctor && arity == 3 ->
                        PermissionError(
                            actualMessage,
                            cause,
                            contexts,
                            Operation.fromTerm(getArgAt(0))!!,
                            Permission.fromTerm(getArgAt(1))!!,
                            getArgAt(2),
                            extraData
                        )
                    else -> customError(message, cause, contexts, type, extraData)
                }
            } catch (_: NullPointerException) {
                customError(message, cause, contexts, type, extraData)
            }
        }
    }
}
