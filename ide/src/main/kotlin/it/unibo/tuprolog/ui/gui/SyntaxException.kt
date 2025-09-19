package it.unibo.tuprolog.ui.gui

import it.unibo.tuprolog.core.exception.TuPrologException
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.solve.exception.error.SyntaxError
import java.io.File

internal sealed class SyntaxException(
    override val cause: ParseException,
) : TuPrologException(cause = cause) {
    class InTheorySyntaxError(
        val file: File,
        cause: ParseException,
    ) : SyntaxException(cause) {
        override val message: String
            get() {
                val errorDetector =
                    SyntaxError.errorDetector(
                        cause.input?.toString() ?: file.readText(),
                        cause.line,
                        cause.column,
                        cause.message,
                    )
                return """
                    |Syntax error at ${cause.line}:${cause.column} of ${file.name}, while parsing clause ${cause.clauseIndex}
                    |
                    |    ${errorDetector.replace("\n", "\n|    ")}
                    """.trimMargin()
            }
    }

    class InQuerySyntaxError(
        val query: String,
        cause: ParseException,
    ) : SyntaxException(cause) {
        override val message: String
            get() {
                if (query.isEmpty()) {
                    return "Query cannot be empty"
                }
                val errorDetector = SyntaxError.errorDetector(query, cause.line, cause.column, cause.message)
                return """
                    |Syntax error in query, near column ${cause.column}
                    |
                    |    ${errorDetector.replace("\n", "\n|    ")}
                    """.trimMargin()
            }
    }

    abstract override val message: String
}
