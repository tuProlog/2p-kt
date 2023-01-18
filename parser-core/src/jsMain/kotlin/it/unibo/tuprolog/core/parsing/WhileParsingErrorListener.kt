package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.parser.ErrorListener

@Suppress("unused")
class WhileParsingErrorListener(private val whileParsing: Any) : ErrorListener() {
    private val delegate = WhileParsingErrorListerWorkaround(this)

    override fun syntaxError(
        recognizer: dynamic,
        offendingSymbol: dynamic,
        line: Int,
        column: Int,
        msg: String,
        e: dynamic
    ) = delegate.syntaxError(recognizer, offendingSymbol, line, column, msg, e)

    override fun reportAmbiguity(
        recognizer: dynamic,
        dfa: dynamic,
        startIndex: Int,
        stopIndex: Int,
        exact: dynamic,
        ambigAlts: dynamic,
        configs: dynamic
    ) = delegate.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)

    override fun reportAttemptingFullContext(
        recognizer: dynamic,
        dfa: dynamic,
        startIndex: Int,
        stopIndex: Int,
        conflictingAlts: dynamic,
        configs: dynamic
    ) = delegate.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)

    override fun reportContextSensitivity(
        recognizer: dynamic,
        dfa: dynamic,
        startIndex: Int,
        stopIndex: Int,
        prediction: dynamic,
        configs: dynamic
    ) = delegate.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
}
