package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.exception.error.SyntaxError

expect sealed class SyntaxException(cause: ParseException) : TuPrologException {
    abstract override val message: String
    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}

expect class InTheorySyntaxError(page: PageID, text: String, cause: ParseException) : SyntaxException {
    val page: PageID

    val text: String

    override val message: String
}

expect class InQuerySyntaxError(query: String, cause: ParseException) : SyntaxException {
    val query: String
    override val message: String
}

internal fun ParseException.toTheoryMessage(page: PageID, text: String): String {
    val errorDetector = SyntaxError.errorDetector(input?.toString() ?: text, line, column, message)
    return """
        |Syntax error at $line:$column of ${page.name}, while parsing clause $clauseIndex:
        |
        |    ${errorDetector.replace("\n", "\n|    ")}
        """.trimMargin()
}

internal fun ParseException.toQueryMessage(query: String): String {
    if (query.isEmpty()) {
        return "Query cannot be empty"
    }
    val errorDetector = SyntaxError.errorDetector(query, line, column, message)
    return """
        |Syntax error in query, near column $column:
        |
        |    ${errorDetector.replace("\n", "\n|    ")}
        """.trimMargin()
}
