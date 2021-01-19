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
 * The domain error occurs when something has the correct type but the value is not admissible
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param expectedDomain The expected domain, that wouldn't have raised the error
 * @param culprit The value not respecting [expectedDomain]
 * @param extraData The possible extra data to be carried with the error
 */
@Suppress("MemberVisibilityCanBePrivate")
class DomainError(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    @JsName("expectedDomain") val expectedDomain: Expected,
    @JsName("culprit") val culprit: Term,
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        expectedDomain: Expected,
        actualValue: Term,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), expectedDomain, actualValue, extraData)

    override fun updateContext(newContext: ExecutionContext): DomainError =
        DomainError(message, cause, contexts.setFirst(newContext), expectedDomain, culprit, extraData)

    override fun pushContext(newContext: ExecutionContext): DomainError =
        DomainError(message, cause, contexts.addLast(newContext), expectedDomain, culprit, extraData)

    override val type: Struct by lazy { Struct.of(super.type.functor, expectedDomain.toTerm(), culprit) }

    companion object {

        @JsName("forArgument")
        @JvmStatic
        fun forArgument(
            context: ExecutionContext,
            procedure: Signature,
            expectedDomain: Expected,
            actualValue: Term,
            index: Int? = null
        ): DomainError = message(
            (index?.let { "The $it-th argument" } ?: "An argument") +
                "of `${procedure.pretty()}` should be `$expectedDomain`, " +
                "but `${actualValue.pretty()}` has been provided instead"
        ) { m, extra ->
            DomainError(
                message = m,
                context = context,
                expectedDomain = expectedDomain,
                actualValue = actualValue,
                extraData = extra
            )
        }

        @JsName("forTerm")
        @JvmStatic
        fun forTerm(
            context: ExecutionContext,
            expectedDomain: Expected,
            actualValue: Term
        ): DomainError = message(
            "Term `${actualValue.pretty()}` is not a valid $expectedDomain"
        ) { m, extra ->
            DomainError(
                message = m,
                context = context,
                expectedDomain = expectedDomain,
                actualValue = actualValue,
                extraData = extra
            )
        }

        @JsName("forGoal")
        @JvmStatic
        fun forGoal(
            context: ExecutionContext,
            procedure: Signature,
            expectedDomain: Expected,
            actualValue: Term
        ): DomainError = message(
            "Subgoal `${actualValue.pretty()}` of ${procedure.pretty()} is not $expectedDomain term"
        ) { m, extra ->
            DomainError(
                message = m,
                context = context,
                expectedDomain = expectedDomain,
                actualValue = actualValue,
                extraData = extra
            )
        }

        /** The domain error Struct functor */
        const val typeFunctor = "domain_error"
    }

    /**
     * A class describing the expected domain whose absence caused the error
     */
    enum class Expected : ToTermConvertible {

        ATOM_PROPERTY,
        BUFFERING_MODE,
        CHARACTER_CODE_LIST,
        CLOSE_OPTION,
        DATE_TIME,
        EOF_ACTION,
        FLAG_VALUE,
        FORMAT_CONTROL_SEQUENCE,
        IO_MODE,
        NON_EMPTY_LIST,
        NOT_LESS_THAN_ZERO,
        OPERATOR_PRIORITY,
        OPERATOR_SPECIFIER,
        ORDER,
        OS_FILE_PERMISSION,
        OS_FILE_PROPERTY,
        OS_PATH,
        PREDICATE_PROPERTY,
        PROLOG_FLAG,
        READ_OPTION,
        SELECTABLE_ITEM,
        SOCKET_ADDRESS,
        SOCKET_DOMAIN,
        SOURCE_SINK,
        STREAM,
        STREAM_OPTION,
        STREAM_OR_ALIAS,
        STREAM_POSITION,
        STREAM_PROPERTY,
        STREAM_SEEK_METHOD,
        STREAM_TYPE,
        TERM_STREAM_OR_ALIAS,
        VAR_BINDING_OPTION,
        WRITE_OPTION,
        CLAUSE,
        RULE,
        FACT,
        DIRECTIVE;

        /** The expected domain string description */
        @JsName("domain")
        val domain: String by lazy { name.toLowerCase() }

        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(domain)

        override fun toString(): String = domain

        companion object {

            /** Returns the Expected instance described by [domain]; creates a new instance only if [domain] was not predefined */
            @JsName("of")
            @JvmStatic
            fun of(domain: String): Expected = valueOf(domain.toUpperCase())

            /** Gets [Expected] instance from [term] representation, if possible */
            @JsName("fromTerm")
            @JvmStatic
            fun fromTerm(term: Term): Expected? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }
}
