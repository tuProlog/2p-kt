package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * The syntax error occurs when a sequence of characters being read does not conform to the grammar of Prolog terms,
 * e.g. while parsing a source file or a `read_term`-style built-in's input.
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 * @param contexts a stack of contexts localising the exception
 * @param extraData The possible extra data to be carried with the error
 */
class SyntaxError constructor(
    message: String? = null,
    cause: Throwable? = null,
    contexts: Array<ExecutionContext>,
    extraData: Term? = null,
) : LogicError(message, cause, contexts, Atom.of(typeFunctor), extraData) {
    constructor(
        message: String? = null,
        cause: Throwable? = null,
        context: ExecutionContext,
        extraData: Term? = null,
    ) : this(message, cause, arrayOf(context), extraData)

    override fun updateContext(
        newContext: ExecutionContext,
        index: Int,
    ): SyntaxError = SyntaxError(message, cause, contexts.setItem(index, newContext), extraData)

    override fun updateLastContext(newContext: ExecutionContext): SyntaxError =
        updateContext(
            newContext,
            contexts.lastIndex,
        )

    override fun pushContext(newContext: ExecutionContext): SyntaxError =
        SyntaxError(message, cause, contexts.addLast(newContext), extraData)

    companion object {
        /** The system error Struct functor */
        @Suppress("ConstPropertyName", "ktlint:standard:property-naming")
        const val typeFunctor = "syntax_error"

        /** Creates a [SyntaxError] with a plain [message], no source position information attached. */
        @JsName("of")
        @JvmStatic
        fun of(
            context: ExecutionContext,
            message: String,
        ): SyntaxError =
            message("Syntax error: $message") { m, extra ->
                SyntaxError(
                    message = m,
                    context = context,
                    extraData = extra,
                )
            }

        /** Creates a [SyntaxError] reporting that parsing [input] as a single term failed at [row]:[column], with detail [message]. */
        @JsName("whileParsingTerm")
        @JvmStatic
        fun whileParsingTerm(
            context: ExecutionContext,
            input: String,
            row: Int,
            column: Int,
            message: String,
        ): SyntaxError =
            message("Syntax error at $row:$column while parsing `$input`: $message") { m, extra ->
                SyntaxError(
                    message = m,
                    context = context,
                    extraData = extra,
                )
            }

        /**
         * Creates a [SyntaxError] reporting that parsing the [index]-th clause of [input] failed at [row]:[column],
         * with detail [message]; the error message includes a caret (`^`) pointing at the offending column, produced
         * via [errorDetector].
         */
        @JsName("whileParsingClauses")
        @JvmStatic
        fun whileParsingClauses(
            context: ExecutionContext,
            input: String,
            index: Int,
            row: Int,
            column: Int,
            message: String,
        ): SyntaxError =
            message(
                """
                |Syntax error at $row:$column while parsing clause $index: $message
                |   ${errorDetector(input, row, column, message).replace("\n", "\n|   ")}
                """.trimMargin(),
            ) { m, extra ->
                SyntaxError(
                    message = m,
                    context = context,
                    extraData = extra,
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

        /**
         * Renders [text]'s [line] (and, for context, [line] `- 1` if present), followed by a `^` marker under
         * [column] and the optional [message], for use in a human-readable [SyntaxError] report.
         */
        @JsName("errorDetector")
        @JvmStatic
        fun errorDetector(
            text: String,
            line: Int,
            column: Int,
            message: String? = null,
        ): String {
            val lines =
                text
                    .lineSequence()
                    .drop(line - 1)
                    .take(1)
                    .toList()
            if (lines.isEmpty()) return text
            val padding = kotlin.math.max(line.log10(), (line - 1).log10())
            val prefix =
                if (line > 1) {
                    "${(line - 1).toString().padStart(padding)}: ...\n"
                } else {
                    ""
                }
            val culprit = "${line.toString().padStart(padding)}: ${lines.last()}\n"
            val detector = "".padStart(padding + column + 1) + "^ " + (message ?: "")
            return prefix + culprit + detector
        }
    }
}
