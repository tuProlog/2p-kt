package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Term
import kotlin.reflect.KClass

class InvalidTermTypeException(
    input: Any?,
    val term: Term,
    val type: KClass<out Term>,
    offendingSymbol: String = input as? String ?: term.toString(),
    line: Int = 1,
    column: Int = 1,
    message: String? = "Expected ${type.simpleName}, got: $offendingSymbol",
    throwable: Throwable? = null
) : ParseException(input, offendingSymbol, line, column, message, throwable)
