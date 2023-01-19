package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.exception.error.SyntaxError

sealed class SyntaxException(override val cause: ParseException) : TuPrologException(cause = cause) {
    @Suppress("MemberVisibilityCanBePrivate")
    class InTheorySyntaxError(val page: PageID, val text: String, cause: ParseException) : SyntaxException(cause) {
        override val message: String
            get() {
                val errorDetector = SyntaxError.errorDetector(
                    cause.input?.toString() ?: text,
                    cause.line,
                    cause.column,
                    cause.message
                )
                return """
                    |Syntax error at ${cause.line}:${cause.column} of ${page.name}, while parsing clause ${cause.clauseIndex}:
                    |
                    |    ${errorDetector.replace("\n", "\n|    ")}
                    """.trimMargin()
            }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class InQuerySyntaxError(val query: String, cause: ParseException) : SyntaxException(cause) {
        override val message: String
            get() {
                if (query.isEmpty()) {
                    return "Query cannot be empty"
                }
                val errorDetector = SyntaxError.errorDetector(query, cause.line, cause.column, cause.message)
                return """
                    |Syntax error in query, near column ${cause.column}:
                    |
                    |    ${errorDetector.replace("\n", "\n|    ")}
                    """.trimMargin()
            }
    }

    abstract override val message: String
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SyntaxException

        if (cause != other.cause) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cause.hashCode()
        result = 31 * result + message.hashCode()
        return result
    }
}
