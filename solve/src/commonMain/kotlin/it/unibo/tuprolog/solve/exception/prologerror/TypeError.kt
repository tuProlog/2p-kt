package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
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
        context: ExecutionContext,
        val expectedType: Expected,
        val actualValue: Term,
        extraData: Term? = null
) : PrologError(message, cause, context, Atom.of("type_error"), extraData) {

    /** The possible expected types */
    enum class Expected {
        CALLABLE, ATOM, INTEGER, PREDICATE_INDICATOR, COMPOUND, LIST, CHARACTER;

        // these are only some of the commonly used types... when implementing more built-ins types can be added
        // maybe in future "type" information, as it is described in PrologStandard, could be moved in a standalone "enum class" and used here

        /** A function to transform the type to corresponding [Atom] representation */
        fun toAtom(): Atom = Atom.of(toString().toLowerCase())
    }

    override val type: Struct by lazy { Struct.of(super.type.functor, expectedType.toAtom(), actualValue) }
}