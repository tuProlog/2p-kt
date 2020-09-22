@file:JsModule("antlr4/error")
@file:JsNonModule

package it.unibo.tuprolog.parser

abstract external class ErrorStrategy {
    fun reset(recognizer: dynamic)
    fun recoverInline(recognizer: dynamic)
    fun recover(recognizer: dynamic)
    fun sync(recognizer: dynamic)
    fun inErrorRecoveryMode(recognizer: dynamic)
    fun reportError(recognizer: dynamic)
}

open external class DefaultErrorStrategy : ErrorStrategy {
    val errorRecoveryMode: Boolean
    val lastErrorIndex: Int
    val lastErrorStates: dynamic
}

open external class BailErrorStrategy : DefaultErrorStrategy

abstract external class ErrorListener {
    abstract fun syntaxError(recognizer: dynamic, offendingSymbol: dynamic, line: Int, column: Int, msg: String, e: dynamic)
}

open external class ConsoleErrorListener : ErrorListener {
    override fun syntaxError(recognizer: dynamic, offendingSymbol: dynamic, line: Int, column: Int, msg: String, e: dynamic)
}

external class DiagnosticErrorListener : ErrorListener {
    val exactOnly: Boolean

    constructor(exactOnly: Boolean)
    constructor()

    override fun syntaxError(recognizer: dynamic, offendingSymbol: dynamic, line: Int, column: Int, msg: String, e: dynamic)
}

open external class RecognitionException(params: dynamic) : Throwable {
    val recognizer: dynamic
    val input: dynamic
    val ctx: dynamic
    val offendingToken: Token?

    fun getExpectedTokens(): dynamic
}

open external class ParseCancellationException : Throwable
