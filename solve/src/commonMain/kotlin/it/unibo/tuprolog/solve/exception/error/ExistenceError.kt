package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.LogicError
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * The existence error occurs when an object on which an operation is to be performed does not exist
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param expectedObject The type of the missing object
 * @param culprit The object whose lack caused the error
 * @param extraData The possible extra data to be carried with the error
 */
class ExistenceError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("expectedObject") val expectedObject: ObjectType,
    @JsName("culprit") val culprit: Term,
    extraData: Term? = null
) : LogicError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        expectedObject: ObjectType,
        actualValue: Term,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), expectedObject, actualValue, extraData)

    override val type: Struct by lazy { Struct.of(super.type.functor, expectedObject.toTerm(), culprit) }

    override fun updateContext(newContext: ExecutionContext, index: Int): ExistenceError =
        ExistenceError(message, cause, contexts.setItem(index, newContext), expectedObject, culprit, extraData)

    override fun updateLastContext(newContext: ExecutionContext): ExistenceError =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): ExistenceError =
        ExistenceError(message, cause, contexts.addLast(newContext), expectedObject, culprit, extraData)

    companion object {

        @JsName("of")
        @JvmStatic
        fun of(
            context: ExecutionContext,
            expectedType: ObjectType,
            culprit: Term,
            message: String
        ) = ExistenceError(
            message = message,
            context = context,
            expectedObject = expectedType,
            actualValue = culprit,
            extraData = Atom.of(message)
        )

        @JsName("forProcedure")
        @JvmStatic
        fun forProcedure(
            context: ExecutionContext,
            procedure: Signature
        ) = message(
            "Procedure `${procedure.pretty()}` does not exist"
        ) { m, extra ->
            ExistenceError(
                message = m,
                context = context,
                expectedObject = ObjectType.PROCEDURE,
                actualValue = procedure.toIndicator(),
                extraData = extra
            )
        }

        @JsName("forSourceSink")
        @JvmStatic
        fun forSourceSink(
            context: ExecutionContext,
            alias: Atom
        ) = forSourceSink(context, alias.value)

        @JsName("forSourceSinkWithAlias")
        @JvmStatic
        fun forSourceSink(
            context: ExecutionContext,
            alias: String
        ) = message(
            "There exists no stream whose alias is `$alias`"
        ) { m, extra ->
            ExistenceError(
                message = m,
                context = context,
                expectedObject = ObjectType.SOURCE_SINK,
                actualValue = Atom.of(alias),
                extraData = extra
            )
        }

        @JsName("forStream")
        @JvmStatic
        fun forStream(
            context: ExecutionContext,
            steamTerm: Struct
        ) = message(
            "No such a stream exists: $steamTerm"
        ) { m, extra ->
            ExistenceError(
                message = m,
                context = context,
                expectedObject = ObjectType.STREAM,
                actualValue = steamTerm,
                extraData = extra
            )
        }

        @JsName("forResource")
        @JvmStatic
        fun forResource(
            context: ExecutionContext,
            name: String
        ) = message(
            "There exists no such a resource `$name`"
        ) { m, extra ->
            ExistenceError(
                message = m,
                context = context,
                expectedObject = ObjectType.RESOURCE,
                actualValue = Atom.of(name),
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
        RESOURCE,
        STREAM,
        OOP_ALIAS,
        OOP_METHOD,
        OOP_CONSTRUCTOR,
        OOP_PROPERTY;

        /** The type expected string description */
        private val type: String by lazy { name.lowercase() }

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(type)

        override fun toString(): String = type

        companion object {

            /** Returns the [ObjectType] instance described by [type]; creates a new instance only if [type] was not predefined */
            @JsName("of")
            @JvmStatic
            fun of(type: String): ObjectType = valueOf(type)

            /** Gets [ObjectType] instance from [term] representation, if possible */
            @JsName("fromTerm")
            @JvmStatic
            fun fromTerm(term: Term): ObjectType? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }
}
