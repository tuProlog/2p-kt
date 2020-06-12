package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError

/**
 * The domain error occurs when something has the correct type but the value is not amissible
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param context The current context at exception creation
 * @param expectedDomain The expected domain, that wouldn't have raised the error
 * @param actualValue The value not respecting [expectedDomain]
 * @param extraData The possible extra data to be carried with the error
 *
 * @author Enrico
 */
class DomainError(
    message: String? = null,
    cause: Throwable? = null,
    context: ExecutionContext,
    val expectedDomain: Expected,
    val actualValue: Term,
    extraData: Term? = null
) : PrologError(message, cause, context, Atom.of(typeFunctor), extraData) {

    override val type: Struct by lazy { Struct.of(super.type.functor, expectedDomain.toTerm(), actualValue) }

    companion object {

        fun forArgument(
            context: ExecutionContext,
            procedure: Signature,
            expectedDomain: Expected,
            actualValue: Term,
            index: Int? = null
        ) = DomainError(
            message = "Argument ${index?.toString()?.plus(" ") ?: ""}" +
                    "of `${procedure.toIndicator()}` should be `$expectedDomain`, " +
                    "but `$actualValue` has been provided instead",
            context = context,
            expectedDomain = expectedDomain,
            actualValue = actualValue,
            extraData = actualValue
        )

        fun forGoal(
            context: ExecutionContext,
            procedure: Signature,
            expectedDomain: Expected,
            actualValue: Term
        ) = "Subgoal `$actualValue` of ${procedure.toIndicator()} is not $expectedDomain term".let {
            DomainError(
                message = it,
                context = context,
                expectedDomain = expectedDomain,
                actualValue = actualValue,
                extraData = Atom.of(it)
            )
        }

        /** The domain error Struct functor */
        const val typeFunctor = "domain_error"
    }

    /**
     * A class describing the expected domain whose absence caused the error
     *
     * @param domain the expected domain string description
     */
    enum class Expected constructor(private val domain: String) : ToTermConvertible {
        // TODO add more cases
        NOT_LESS_THAN_ZERO("not_less_than_zero"),
        STREAM_PROPERTY("stream_property"),
        STREAM_POSITION("stream_position"),
        STREAM_OR_ALIAS("stream_or_alias"),
        READ_OPTION("read_option"),
        PROLOG_FLAG("prolog_flag"),
        FLAG_VALUE("flag_value"),
        OPERATOR_SPECIFIER("operator_specifier"),
        OPERATOR_PRIORITY("operator_priority");


        /** A function to transform the type to corresponding [Atom] representation */
        override fun toTerm(): Atom = Atom.of(domain)

        override fun toString(): String = domain

        companion object {

            /** Returns the Expected instance described by [domain]; creates a new instance only if [domain] was not predefined */
            fun of(domain: String): Expected = valueOf(domain.toUpperCase())

            /** Gets [Expected] instance from [term] representation, if possible */
            fun fromTerm(term: Term): Expected? = when (term) {
                is Atom -> of(term.value)
                else -> null
            }
        }
    }
}