package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError

/**
 * The existence error occurs when an object on which an operation is to be performed does not exist
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param expectedType The type of the missing object
 * @param actualValue The object whose lack caused the error
 * @param extraData The possible extra data to be carried with the error
 */
class ExistenceError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    val expectedType: ObjectType,
    val actualValue: Term,
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        expectedType: ObjectType,
        actualValue: Term,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), expectedType, actualValue, extraData)

    override val type: Struct by lazy { Struct.of(super.type.functor, expectedType.toTerm(), actualValue) }

    override fun updateContext(newContext: ExecutionContext): ExistenceError =
        ExistenceError(message, cause, contexts.setFirst(newContext), expectedType, actualValue, extraData)

    override fun pushContext(newContext: ExecutionContext): ExistenceError =
        ExistenceError(message, cause, contexts.addLast(newContext), expectedType, actualValue, extraData)

    companion object {

        fun of(
            context: ExecutionContext,
            expectedType: ObjectType,
            culprit: Term,
            message: String
        ) = ExistenceError(
            message = message,
            context = context,
            expectedType = expectedType,
            actualValue = culprit,
            extraData = Atom.of(message)
        )

        fun forProcedure(
            context: ExecutionContext,
            procedure: Signature
        ) = message(
            "Procedure `${procedure.pretty()}` does not exist"
        ) { m, extra ->
            ExistenceError(
                message = m,
                context = context,
                expectedType = ObjectType.PROCEDURE,
                actualValue = procedure.toIndicator(),
                extraData = extra
            )
        }

        fun forStream(
            context: ExecutionContext,
            alias: String
        ) = forStream(context, Atom.of(alias))

        fun forStream(
            context: ExecutionContext,
            alias: Atom
        ) = message(
            "There exists no stream whose alias is `$alias`"
        ) { m, extra ->
            ExistenceError(
                message = m,
                context = context,
                expectedType = ObjectType.STREAM,
                actualValue = alias,
                extraData = extra
            )
        }

        /** The existence error Struct functor */
        const val typeFunctor = "existence_error"
    }

    /**
     * A class describing the expected type whose absence caused the error
     */
    enum class ObjectType : ToTermConvertible {

        PROCEDURE,
        SOURCE_SINK,
        STREAM,
        OOP_ALIAS,
        OOP_METHOD,
        OOP_CONSTRUCTOR,
        OOP_PROPERTY;

        /** The type expected string description */
        private val type: String by lazy { name.toLowerCase() }

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(type)

        override fun toString(): String = type

        companion object {

            /** Returns the [ObjectType] instance described by [type]; creates a new instance only if [type] was not predefined */
            fun of(type: String): ObjectType = valueOf(type)

            /** Gets [ObjectType] instance from [term] representation, if possible */
            fun fromTerm(term: Term): ObjectType? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }
}
