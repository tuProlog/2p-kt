@file:JsModule("antlr4")
@file:JsNonModule

package it.unibo.tuprolog.parser

/**
 * @see https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/Token.js
 */
open external class Token {
    var source: dynamic
    var type: Int; // token type of the token
    var channel: Int; // The parser ignores everything not on DEFAULT_CHANNEL
    var start: Int // optional; return -1 if not implemented.
    var stop: Int; // optional; return -1 if not implemented.
    var tokenIndex: Int; // from 0..n-1 of the token object in the input stream
    var line: Int; // line=1..n of the 1st character
    var column: Int; // beginning of the line at which it occurs, 0..n-1
    var text: String; // text of the token.

    companion object {
        val INVALID_TYPE: Int
        val EPSILON: Int
        val EOF: Int
        val DEFAULT_CHANNEL: Int
        val HIDDEN_CHANNEL: Int
    }
}

/**
 * @see https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/Token.js
 */
external class CommonToken(source: dynamic, type: Int, channel: Int, start: Int, stop: Int) : Token {

    fun clone(): CommonToken

    override fun toString(): String

    companion object {
        val EMPTY_SOURCE: dynamic
    }
}

/**
 * @see https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/InputStream.js
 */
external class InputStream(input: dynamic, decodeToUnicodeCodePoints: Boolean) {
    constructor(input: dynamic)

    val name: String
    val index: Int
    val size: Int

    fun reset()
    fun consume()
    fun LA(offset: Int): Int
    fun LT(offset: Int): Int
    fun mark(): Int
    fun release()
    fun seek(index: Int)
    fun getText(start: Int, stop: Int): String

    override fun toString(): String
}

/**
 * @see https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/BufferedTokenStream.js
 */
open external class TokenStream

/**
 * https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/BufferedTokenStream.js
 */
open external class BufferedTokenStream(tokenSource: dynamic) : TokenStream {
    val tokenSource: dynamic
    val tokens: Array<Token>
    val index: Int
    val fetchedEOF: Boolean

    fun mark(): Int
    fun release(marker: Int)
    fun reset()
    fun seek(index: Int)
    operator fun get(index: Int): Token
    fun consume()
    fun sync(index: Int): Boolean
    fun fetch(n: Int): Int
    fun getTokens(start: Int, stop: Int)
    fun LA(offset: Int): Int
    open fun LB(offset: Int): Int
    open fun LT(offset: Int): Int
    fun fill()
}

/**
 * @see https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/CommonTokenStream.js
 */
external class CommonTokenStream(lexer: dynamic, channel: Int) : BufferedTokenStream {
    constructor(lexer: dynamic)

    override fun LB(offset: Int): Int
    override fun LT(offset: Int): Int
    fun getNumberOfChannelTokens(): Int

    val channel: Int
}

/**
 * @see https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/RuleContext.js
 * */
open external class RuleContext(parent: dynamic, invokingStateNumber: Int) {
    fun <T> accept(visitor: dynamic): T
}

/**
 * @see https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/ParserRuleContext.js
 * */
open external class ParserRuleContext(parent: dynamic, invokingStateNumber: Int) : RuleContext {
    val children: Array<dynamic>

    val start: Token?

    val stop: Token?

    val exception: dynamic
}
