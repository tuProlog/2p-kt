package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Term
import kotlin.reflect.KClass

/**
 * Indicates that valid Prolog syntax produced a term of a different type than the caller requested.
 *
 * For example, [TermParser.parseInteger] throws this exception when given the valid atom `foo`.
 * This is a [ParseException], allowing callers to handle syntax failures and type mismatches at the
 * same API boundary while still inspecting [term] and [type] when needed.
 *
 * @param input original input or source identifier
 * @property term successfully parsed term whose runtime type did not match [type]
 * @property type requested term type
 * @param offendingSymbol textual representation used in the diagnostic
 * @param line one-based source line, when known
 * @param column one-based source column, when known
 * @param message human-readable diagnostic
 * @param throwable underlying cause, if any
 */
class InvalidTermTypeException(
    input: Any?,
    val term: Term,
    val type: KClass<out Term>,
    offendingSymbol: String = input as? String ?: term.toString(),
    line: Int = 1,
    column: Int = 1,
    message: String? = "Expected ${type.simpleName}, got: $offendingSymbol",
    throwable: Throwable? = null,
) : ParseException(input, offendingSymbol, line, column, message, throwable)
