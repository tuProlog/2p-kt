package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A permission error occurs when an attempt to perform a prohibited operation is made
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param operation the operation which caused the error
 * @param permission the type of the tried permission
 * @param extraData the possible extra data to be carried with the error
 */
@Suppress("MemberVisibilityCanBePrivate")
class PermissionError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("operation") val operation: Operation,
    @JsName("permission") val permission: Permission,
    @JsName("culprit") val culprit: Term,
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        operation: Operation,
        permission: Permission,
        culprit: Term,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), operation, permission, culprit, extraData)

    override fun updateContext(newContext: ExecutionContext, index: Int): PermissionError =
        PermissionError(message, cause, contexts.setItem(index, newContext), operation, permission, culprit, extraData)

    override fun updateLastContext(newContext: ExecutionContext): PermissionError =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): PermissionError =
        PermissionError(message, cause, contexts.addLast(newContext), operation, permission, culprit, extraData)

    override val type: Struct by lazy {
        Struct.of(
            super.type.functor,
            operation.toTerm(),
            permission.toTerm(),
            culprit
        )
    }

    companion object {

        @JsName("of")
        @JvmStatic
        fun of(
            context: ExecutionContext,
            procedure: Signature,
            operation: Operation,
            permission: Permission,
            culprit: Term
        ): PermissionError = message(
            "Permission error while executing ${procedure.pretty()}: " +
                "operation of type `$operation` is not possible on $permission: ${culprit.pretty()}"
        ) { m, extra ->
            PermissionError(
                message = m,
                context = context,
                operation = operation,
                permission = permission,
                culprit = culprit,
                extraData = extra
            )
        }

        /** The permission error Struct functor */
        const val typeFunctor = "permission_error"
    }

    /**
     * A class describing the operation which caused the error
     */
    enum class Operation : ToTermConvertible {
        ACCESS,
        ADD_ALIAS,
        CLOSE,
        CREATE,
        INPUT,
        INVOKE,
        MODIFY,
        OPEN,
        OUTPUT,
        REPOSITION;

        @JsName("operation")
        val operation: String by lazy { this.name.lowercase() }

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(operation)

        override fun toString(): String = operation

        companion object {

            /** Returns the [Operation] instance described by [operation]; creates a new instance only if [operation] was not predefined */
            @JsName("of")
            @JvmStatic
            fun of(operation: String): Operation = valueOf(operation.uppercase())

            /** Gets [Operation] instance from [term] representation, if possible */
            @JsName("fromTerm")
            @JvmStatic
            fun fromTerm(term: Term): Operation? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }

    /**
     * A class describing the type of the tried permission
     */
    enum class Permission : ToTermConvertible {
        BINARY_STREAM,
        FLAG,
        OPERATOR,
        PAST_END_OF_STREAM,
        PRIVATE_PROCEDURE,
        SOURCE_SINK,
        STATIC_PROCEDURE,
        OOP_METHOD,
        STREAM,
        TEXT_STREAM;

        @JsName("permission")
        val permission: String by lazy { this.name.lowercase() }

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(permission)

        override fun toString(): String = permission

        companion object {

            /** Returns the [Permission] instance described by [permission]; creates a new instance only if [permission] was not predefined */
            @JsName("of")
            @JvmStatic
            fun of(permission: String): Permission = valueOf(permission.uppercase())

            /** Gets [Permission] instance from [term] representation, if possible */
            @JsName("fromTerm")
            @JvmStatic
            fun fromTerm(term: Term): Permission? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }
}
