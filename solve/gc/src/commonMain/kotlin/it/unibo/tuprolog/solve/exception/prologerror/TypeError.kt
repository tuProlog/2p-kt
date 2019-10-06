package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError.Expected

/**
 * The type error occurs when something is not of [Expected] type
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param expectedType The type expected, that wouldn't have raised the error
 * @param actualValue The value not respecting [expectedType]
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class TypeError(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext? = null,
        val expectedType: Expected,
        val actualValue: Term,
        extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    constructor(index: Int, expectedType: Expected, actualValue: Term, signature: Signature)
            : this(
            message = "Argument $index of $signature should be a ${expectedType.name.toLowerCase()}, but $actualValue has been provided instead",
            expectedType = expectedType,
            actualValue = actualValue,
            extraData = Atom.of("\"Argument $index of $signature should be a ${expectedType.name.toLowerCase()}, but $actualValue has been provided instead\",")
    )

    override val type: Struct by lazy { Struct.of(super.type.functor, expectedType.toAtom(), actualValue) }

    companion object {

        /** The type error Struct functor */
        const val typeFunctor = "type_error"
    }

    /**
     * The possible expected types
     *
     * @author Enrico
     */
    enum class Expected {
        CALLABLE, ATOM, INTEGER, NUMBER, PREDICATE_INDICATOR, COMPOUND, LIST, CHARACTER;

        // these are only some of the commonly used types... when implementing more built-ins types can be added
        // maybe in future "type" information, as it is described in PrologStandard, could be moved in a standalone "enum class" and used here

        /** A function to transform the type to corresponding [Atom] representation */
        fun toAtom(): Atom = Atom.of(toString().toLowerCase())

        companion object {

            /** Gets [Expected] instance from [term] representation, if possible */
            fun fromTerm(term: Term): Expected? = when (term) {
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