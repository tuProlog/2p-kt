package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Term
import kotlin.reflect.KClass

class InvalidTermTypeException(
    input: Any?,
    offendingSymbol: String,
    expected: KClass<out Term>,
    line: Int,
    column: Int,
    cause: Throwable? = null
) : ParseException(
    input,
    offendingSymbol,
    line,
    column,
    "Expecting ${expected.simpleName}, got `$offendingSymbol` instead",
    cause
)
