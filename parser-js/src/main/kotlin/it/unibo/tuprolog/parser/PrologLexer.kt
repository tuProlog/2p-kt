@file:JsModule("./PrologLexer")
@file:JsNonModule


package it.unibo.tuprolog.parser

external class PrologLexer(input: dynamic) {
    val grammarFileName: String
    val ruleNames: Array<String>
    val symbolicNames: Array<String>
    val literalNames: Array<String>
    val channelNames: Array<String>
    val modeNames: Array<String>
    fun addOperators(vararg operators:String)
    fun getOperators(): Array<String>
    fun isOperator(string: String): Boolean
    fun unquote(string: String):String
    fun escape(string:String,stringType: Int): String
}