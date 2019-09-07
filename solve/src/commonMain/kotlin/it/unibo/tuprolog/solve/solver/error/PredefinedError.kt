package it.unibo.tuprolog.solve.solver.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.solver.error.ErrorUtils.errorStructOf
import it.unibo.tuprolog.solve.solver.error.TypeError.Expected

/**
 * Base class for all predefined error situations in Prolog Standard
 *
 * @author Enrico
 */
sealed class PredefinedError(
        /** String identifying the error situation; will be the principal functor of specific error type */
        val name: String
) {
    /**
     * Generates the error structure of this [PredefinedError] using provided [customErrorData],
     * that's ready to be used as argument for `throw/1`
     *
     * Default implementation creates an [Atom] of error [name] as error description
     */
    open fun toThrowStruct(customErrorData: Struct = Truth.`true`()): Struct =
            errorStructOf(Atom.of(name), customErrorData)
}

/** The instantiation error occurs when some Term is a Variable, and it should not */
object InstantiationError : PredefinedError("instantiation_error")

/** The system error occurs when an internal problem occurred and if not catch, it will halt inferential machine */
object SystemError : PredefinedError("system_error")

/** The type error occurs when something is not of [Expected] type */
data class TypeError(
        /** The type expected, that wouldn't have raised the error */
        val expectedType: Expected,
        /** The value not respecting [expectedType] */
        val actualValue: Term
) : PredefinedError("type_error") {

    /** The possible expected types */
    enum class Expected {
        CALLABLE, ATOM, INTEGER, PREDICATE_INDICATOR, COMPOUND, LIST, CHARACTER;

        // these are only some of the commonly used types... when implementing more built-ins types can be added
        // maybe in future "type" information, as it is described in PrologStandard, could be moved in a standalone "enum class" and used here

        /** A function to transform the type to corresponding [Atom] representation */
        fun toAtom(): Atom = Atom.of(toString().toLowerCase())
    }

    override fun toThrowStruct(customErrorData: Struct): Struct =
            errorStructOf(Struct.of(name, expectedType.toAtom(), actualValue), customErrorData)
}
