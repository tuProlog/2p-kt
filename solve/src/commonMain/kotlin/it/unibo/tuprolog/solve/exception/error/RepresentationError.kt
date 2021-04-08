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
 *  A representation error occurs when an implementation limit has been breached
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param limit the name of the reached limit
 * @param extraData the possible extra data to be carried with the error
 */
@Suppress("MemberVisibilityCanBePrivate")
class RepresentationError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("limit") val limit: Limit,
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        limit: Limit,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), limit, extraData)

    override fun updateContext(newContext: ExecutionContext, index: Int): RepresentationError =
        RepresentationError(message, cause, contexts.setItem(index, newContext), limit, extraData)

    override fun updateLastContext(newContext: ExecutionContext): RepresentationError =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): RepresentationError =
        RepresentationError(message, cause, contexts.addLast(newContext), limit, extraData)

    override val type: Struct by lazy {
        Struct.of(
            super.type.functor,
            limit.toTerm()
        )
    }

    companion object {
        @JsName("of")
        @JvmStatic
        fun of(
            context: ExecutionContext,
            signature: Signature,
            limit: Limit,
            cause: Throwable? = null
        ): RepresentationError = message(
            "Reached representation limit while executing `${signature.pretty()}`: $limit" +
                cause?.message?.let { ". $it" }
        ) { m, extra ->
            RepresentationError(
                message = m,
                context = context,
                limit = limit,
                extraData = extra,
                cause = cause
            )
        }

        /** The permission error Struct functor */
        const val typeFunctor = "representation_error"
    }

    /**
     * Names of possible limits
     */
    enum class Limit : ToTermConvertible {
        CHARACTER,
        CHARACTER_CODE,
        IN_CHARACTER_CODE,
        MAX_ARITY,
        MAX_INTEGER,
        MIN_INTEGER,
        OOP_OBJECT,
        TOO_MANY_VARIABLES;

        @JsName("limit")
        val limit: String by lazy { this.name.toLowerCase() }

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(limit)

        override fun toString(): String = limit

        companion object {

            /** Returns the [Limit] instance described by [limit]; creates a new instance only if [limit] was not predefined */
            @JsName("of")
            @JvmStatic
            fun of(limit: String): Limit = valueOf(limit.toUpperCase())

            /** Gets [Limit] instance from [term] representation, if possible */
            @JsName("fromTerm")
            @JvmStatic
            fun fromTerm(term: Term): Limit? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }
}
