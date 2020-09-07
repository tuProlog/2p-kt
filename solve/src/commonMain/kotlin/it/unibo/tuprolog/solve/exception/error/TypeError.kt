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

        // TODO: 16/01/2020 test factories

        fun forArgumentList(
            context: ExecutionContext,
            procedure: Signature,
            expectedType: Expected,
            actualValue: Term,
            index: Int? = null
        ) = TypeError(
            message = "Argument ${index
                ?: ""} of `${procedure.toIndicator()}` should be a list of `$expectedType`, but `$actualValue` has been provided instead",
            context = context,
            expectedType = expectedType,
            actualValue = actualValue,
            extraData = actualValue
        )

        fun forArgument(
            context: ExecutionContext,
            procedure: Signature,
            expectedType: Expected,
            actualValue: Term,
            index: Int? = null
        ) = TypeError(
            message = "Argument ${index
                ?: ""} of `${procedure.toIndicator()}` should be a `$expectedType`, but `$actualValue` has been provided instead",
            context = context,
            expectedType = expectedType,
            actualValue = actualValue,
            extraData = actualValue
        )

        fun forGoal(
            context: ExecutionContext,
            procedure: Signature,
            expectedType: Expected,
            actualValue: Term
        ) = "Subgoal `$actualValue` of ${procedure.toIndicator()} is not a $expectedType term".let {
            TypeError(
                message = it,
                context = context,
                expectedType = expectedType,
                actualValue = actualValue,
                extraData = Atom.of(it)
            )
        }

        /** The type error Struct functor */
        const val typeFunctor = "type_error"
    }

    /**
     * A class describing the expected type whose absence caused the error
     *
     * @param type the type expected string description
     *
     * @author Enrico
     */
    data class Expected private constructor(private val type: String) : ToTermConvertible {

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(type)

        override fun toString(): String = type

        companion object {

            /** Predefined expected types Atom values */
            private val predefinedExpectedTypes by lazy {
                listOf(
                    "callable", "atom", "integer", "number", "predicate_indicator", "compound",
                    "list", "character", "evaluable"
                )
                // these are only some of the commonly used types... when implementing more built-ins types can be added
                // maybe in future "type" information, as it is described in PrologStandard, could be moved in a standalone "enum class" and used here
            }

            /** Predefined expected instances */
            private val predefinedNameToInstance by lazy { predefinedExpectedTypes.map { it to Expected(it) }.toMap() }

            val CALLABLE by lazy { predefinedNameToInstance.getValue("callable") }
            val ATOM by lazy { predefinedNameToInstance.getValue("atom") }
            val INTEGER by lazy { predefinedNameToInstance.getValue("integer") }
            val NUMBER by lazy { predefinedNameToInstance.getValue("number") }
            val PREDICATE_INDICATOR by lazy { predefinedNameToInstance.getValue("predicate_indicator") }
            val COMPOUND by lazy { predefinedNameToInstance.getValue("compound") }
            val LIST by lazy { predefinedNameToInstance.getValue("list") }
            val CHARACTER by lazy { predefinedNameToInstance.getValue("character") }
            val EVALUABLE by lazy { predefinedNameToInstance.getValue("evaluable") }

            /** Returns the Expected instance described by [type]; creates a new instance only if [type] was not predefined */
            fun of(type: String): Expected = predefinedNameToInstance[type.toLowerCase()]
                ?: Expected(type)

            /** Gets [Expected] instance from [term] representation, if possible */
            fun fromTerm(term: Term): Expected? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }


}