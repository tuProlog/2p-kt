package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.error.TypeError.Expected

/**
 * The type error occurs when something is not of [Expected] type
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param expectedType The type expected, that wouldn't have raised the error
 * @param actualValue The value not respecting [expectedType]
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class TypeError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    val expectedType: Expected,
    val actualValue: Term,
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        expectedType: Expected,
        actualValue: Term,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), expectedType, actualValue, extraData)

    override fun updateContext(newContext: ExecutionContext): TypeError =
        TypeError(message, cause, contexts.setFirst(newContext), expectedType, actualValue, extraData)

    override fun pushContext(newContext: ExecutionContext): TypeError =
        TypeError(message, cause, contexts.addLast(newContext), expectedType, actualValue, extraData)

    override val type: Struct by lazy { Struct.of(super.type.functor, expectedType.toTerm(), actualValue) }

    companion object {

        fun of(
            context: ExecutionContext,
            expectedType: Expected,
            actualValue: Term,
            message: String
        ) = message(message) { m, extra ->
            TypeError(
                message = m,
                context = context,
                expectedType = expectedType,
                actualValue = actualValue,
                extraData = extra
            )
        }

        fun forArgumentList(
            context: ExecutionContext,
            procedure: Signature,
            expectedType: Expected,
            actualValue: Term,
            index: Int? = null
        ) = message(
            (index?.let { "The $it-th argument" } ?: "An argument") +
                " of `${procedure.pretty()}` should be a list of `$expectedType`, " +
                "but `${actualValue.pretty()}` has been provided instead"
        ) { m, extra ->
            TypeError(
                message = m,
                context = context,
                expectedType = expectedType,
                actualValue = actualValue,
                extraData = extra
            )
        }

        fun forArgument(
            context: ExecutionContext,
            procedure: Signature,
            expectedType: Expected,
            actualValue: Term,
            index: Int? = null
        ) =
            message(
                (index?.let { "The $it-th argument" } ?: "An argument") +
                    " of `${procedure.pretty()}` should be a `$expectedType`, " +
                    "but `${actualValue.pretty()}` has been provided instead"
            ) { m, extra ->
                TypeError(
                    message = m,
                    context = context,
                    expectedType = expectedType,
                    actualValue = actualValue,
                    extraData = extra
                )
            }

        fun forGoal(
            context: ExecutionContext,
            procedure: Signature,
            expectedType: Expected,
            actualValue: Term
        ) = message(
            "Subgoal `${actualValue.pretty()}` of ${procedure.pretty()} is not a $expectedType term"
        ) { m, extra ->
            TypeError(
                message = m,
                context = context,
                expectedType = expectedType,
                actualValue = actualValue,
                extraData = extra
            )
        }

        /** The type error Struct functor */
        const val typeFunctor = "type_error"
    }

    /**
     * A class describing the expected type whose absence caused the error
     *
     * @author Enrico
     */
    enum class Expected : ToTermConvertible {
        ATOM,
        ATOMIC,
        BOOLEAN,
        BYTE,
        CALLABLE,
        CHARACTER,
        COMPOUND,
        DEALIASING_EXPRESSION,
        EVALUABLE,
        FLOAT,
        INTEGER,
        LIST,
        NUMBER,
        OBJECT_REFERENCE,
        PAIR,
        PREDICATE_INDICATOR,
        REFERENCE,
        TYPE_REFERENCE;

        /**
         * The type expected string description
         */
        private val type: String by lazy { name.toLowerCase() }

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(type)

        override fun toString(): String = type

        companion object {
            /** Returns the Expected instance described by [type]; creates a new instance only if [type] was not predefined */
            fun of(type: String): Expected = valueOf(type.toUpperCase())

            /** Gets [Expected] instance from [term] representation, if possible */
            fun fromTerm(term: Term): Expected? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }
}
