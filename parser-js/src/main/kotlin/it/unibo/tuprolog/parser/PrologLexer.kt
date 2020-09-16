@file:JsModule("@tuprolog/parser-utils")
@file:JsNonModule

package it.unibo.tuprolog.parser

external class PrologLexer(input: dynamic) {
    val grammarFileName: String
    val ruleNames: Array<String>
    val symbolicNames: Array<String>
    val literalNames: Array<String>
    val channelNames: Array<String>
    val modeNames: Array<String>
    fun addOperators(vararg operators: String)
    fun getOperators(): Array<String>
    fun isOperator(string: String): Boolean
    fun unquote(string: String): String
    fun escape(string: String, stringType: StringType): String
    fun getAllTokens(): Array<Token>

    fun addErrorListener(listener: dynamic)
    fun removeErrorListeners()

    companion object {
        val EOF: Int
        val VARIABLE: Int
        val INTEGER: Int
        val HEX: Int
        val OCT: Int
        val BINARY: Int
        val SIGN: Int
        val FLOAT: Int
        val CHAR: Int
        val BOOL: Int
        val LPAR: Int
        val RPAR: Int
        val LSQUARE: Int
        val RSQUARE: Int
        val EMPTY_LIST: Int
        val LBRACE: Int
        val RBRACE: Int
        val EMPTY_SET: Int
        val SQ_STRING: Int
        val DQ_STRING: Int
        val COMMA: Int
        val PIPE: Int
        val CUT: Int
        val FULL_STOP: Int
        val WHITE_SPACES: Int
        val COMMENT: Int
        val LINE_COMMENT: Int
        val OPERATOR: Int
        val ATOM: Int
    }
}
