package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermConvertible
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * The evaluation error occurs when some problem occurs in evaluating an arithmetic expression
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param errorType The error type
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class EvaluationError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("errorType") val errorType: Type,
    extraData: Term? = null,
) : LogicError(message, cause, contexts, Atom.of(typeFunctor), extraData) {
    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        errorType: Type,
        extraData: Term? = null,
    ) : this(message, cause, arrayOf(context), errorType, extraData)

    override fun updateContext(
        newContext: ExecutionContext,
        index: Int,
    ): EvaluationError = EvaluationError(message, cause, contexts.setItem(index, newContext), errorType, extraData)

    override fun updateLastContext(newContext: ExecutionContext): EvaluationError =
        updateContext(
            newContext,
            contexts.lastIndex,
        )

    override fun pushContext(newContext: ExecutionContext): EvaluationError =
        EvaluationError(message, cause, contexts.addLast(newContext), errorType, extraData)

    override val type: Struct by lazy { Struct.of(super.type.functor, errorType.toTerm()) }

    companion object {
        /** The evaluation error Struct functor */
        @Suppress("ConstPropertyName", "ktlint:standard:property-naming")
        const val typeFunctor = "evaluation_error"
    }

    /**
     * The possible evaluation error types
     *
     * @author Enrico
     */
    enum class Type : TermConvertible {
        INT_OVERFLOW,
        FLOAT_OVERFLOW,
        UNDERFLOW,
        ZERO_DIVISOR,
        UNDEFINED,
        ;

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(toString())

        override fun toString(): String = super.toString().lowercase()

        companion object {
            /** Gets [Type] instance from [term] representation, if possible */
            @JsName("fromTerm")
            @JvmStatic
            fun fromTerm(term: Term): Type? =
                when (term) {
                    is Atom ->
                        try {
                            valueOf(term.value.uppercase())
                        } catch (e: IllegalArgumentException) {
                            null
                        } catch (e: IllegalStateException) {
                            null
                        }
                    else -> null
                }
        }
    }
}
