package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.Token

@Suppress("UNUSED_PARAMETER")
class WhileParsingErrorListerWorkaround(private val whileParsing: Any) {
    private fun symbolToString(obj: dynamic): String =
        if (obj is Token) {
            obj.text
        } else {
            obj.toString()
        }

    @JsName("syntaxError")
    fun syntaxError(
        recognizer: dynamic,
        offendingSymbol: dynamic,
        line: Int,
        column: Int,
        msg: String,
        e: dynamic
    ) {
        if (recognizer is PrologParser) {
            recognizer.removeParseListeners()
        }
        val offending = symbolToString(offendingSymbol)
        throw ParseException(whileParsing, offending, line, column + 1, msg, e)
    }

    @JsName("reportAmbiguity")
    fun reportAmbiguity(recognizer: dynamic, dfa: dynamic, startIndex: Int, stopIndex: Int, exact: dynamic, ambigAlts: dynamic, configs: dynamic) {
        // do nothing
    }

    @JsName("reportAttemptingFullContext")
    fun reportAttemptingFullContext(recognizer: dynamic, dfa: dynamic, startIndex: Int, stopIndex: Int, conflictingAlts: dynamic, configs: dynamic) {
        // do nothing
    }

    @JsName("reportContextSensitivity")
    fun reportContextSensitivity(recognizer: dynamic, dfa: dynamic, startIndex: Int, stopIndex: Int, prediction: dynamic, configs: dynamic) {
        // do nothing
    }
}
