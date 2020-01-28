@file:JsModule("antlr4/BufferedTokenStream")
@file:JsNonModule
package it.unibo.tuprolog.parser

/**
 * https://github.com/antlr/antlr4/blob/master/runtime/JavaScript/src/antlr4/BufferedTokenStream.js
 */
open external class BufferedTokenStream(tokenSource: dynamic) : TokenStream {
    val tokenSource: dynamic
    val tokens: Array<Token>
    val index: Int
    val fetchedEOF: Boolean

    fun mark(): Int
    fun release()
    fun reset()
    fun seek(index: Int)
    operator fun get(index: Int): Token
    fun consume()
    fun sync(index: Int): Boolean
    fun fetch(n: Int): Int
    fun getTokens(start: Int, stop: Int)
    fun LA(offset: Int): Int
    fun LB(offset: Int): Int
    fun LT(offset: Int): Int
    fun fill()
}
