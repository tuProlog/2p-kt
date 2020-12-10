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

    override fun updateContext(newContext: ExecutionContext): SyntaxError =
        SyntaxError(message, cause, contexts.setFirst(newContext), extraData)

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
                |Input source:
                |   ${input.replace("\n", "\n|   ")}
                """.trimMargin()
            ) { m, extra ->
                SyntaxError(
                    message = m,
                    context = context,
                    extraData = extra
                )
            }
    }
}
