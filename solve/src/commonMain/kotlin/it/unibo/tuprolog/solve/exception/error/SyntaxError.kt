package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

class SyntaxError constructor(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    extraData: Term? = null
) : PrologError(message, cause, contexts, Atom.of(typeFunctor), extraData) {

    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        extraData: Term? = null
    ) : this(message, cause, arrayOf(context), extraData)

    override fun updateContext(newContext: ExecutionContext, index: Int): SyntaxError =
        SyntaxError(message, cause, contexts.setItem(index, newContext), extraData)

    override fun updateLastContext(newContext: ExecutionContext): SyntaxError =
        updateContext(newContext, contexts.lastIndex)

    override fun pushContext(newContext: ExecutionContext): SyntaxError =
        SyntaxError(message, cause, contexts.addLast(newContext), extraData)

    companion object {

        /** The system error Struct functor */
        const val typeFunctor = "syntax_error"

        @JsName("of")
        @JvmStatic
        fun of(context: ExecutionContext, message: String): SyntaxError =
            message("Syntax error: $message") { m, extra ->
                SyntaxError(
                    message = m,
                    context = context,
                    extraData = extra
                )
            }

        @JsName("whileParsingTerm")
        @JvmStatic
        fun whileParsingTerm(
            context: ExecutionContext,
            input: String,
            row: Int,
            column: Int,
            message: String
        ): SyntaxError =
            message("Syntax error at $row:$column while parsing `$input`: $message") { m, extra ->
                SyntaxError(
                    message = m,
                    context = context,
                    extraData = extra
                )
            }

        @JsName("whileParsingClauses")
        @JvmStatic
        fun whileParsingClauses(
            context: ExecutionContext,
            input: String,
            index: Int,
            row: Int,
            column: Int,
            message: String
        ): SyntaxError =
            message(
                """
                |Syntax error at $row:$column while parsing clause $index: $message
                |   ${input.errorDetector(row, column, message).replace("\n", "\n|   ")}
                """.trimMargin()
            ) { m, extra ->
                SyntaxError(
                    message = m,
                    context = context,
                    extraData = extra
                )
            }

        private fun Int.log10(): Int {
            var result = 0
            var current = this
            do {
                current /= 10
                result++
            } while (current > 0)
            return result
        }

        private fun String.errorDetector(line: Int, column: Int, message: String? = null): String {
            val lines = this.lineSequence().drop(line - 1).take(1).toList()
            if (lines.isEmpty()) return this
            val padding = kotlin.math.max(line.log10(), (line - 1).log10())
            val prefix = if (line > 1) { "${(line - 1).toString().padStart(padding)}: ...\n" } else ""
            val culprit = "${line.toString().padStart(padding)}: ${lines.last()}\n"
            val detector = "".padStart(padding + column + 1) + "^ " + (message ?: "")
            return prefix + culprit + detector
        }
    }
}
