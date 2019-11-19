package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError

/**
 * The evaluation error occurs when some problem occurs in evaluating an arithmetic expression
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param errorType The error type
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class EvaluationError(
    message: String? = null,
    cause: Throwable? = null,
    context: ExecutionContext,
    val errorType: Type,
    extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    override val type: Struct by lazy { Struct.of(super.type.functor, errorType.toTerm()) }

    companion object {

        /** The evaluation error Struct functor */
        const val typeFunctor = "evaluation_error"
    }

    /**
     * The possible evaluation error types
     *
     * @author Enrico
     */
    enum class Type : ToTermConvertible {
        INT_OVERFLOW, FLOAT_OVERFLOW, UNDERFLOW, ZERO_DIVISOR, UNDEFINED;

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(toString())

        override fun toString(): String = super.toString().toLowerCase()

        companion object {

            /** Gets [Type] instance from [term] representation, if possible */
            fun fromTerm(term: Term): Type? = when (term) {
                is Atom -> try {
                    valueOf(term.value.toUpperCase())
                } catch (e: IllegalArgumentException) {
                    null
                }
                else -> null
            }
        }
    }
}
