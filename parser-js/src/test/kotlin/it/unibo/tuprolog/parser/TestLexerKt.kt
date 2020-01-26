package it.unibo.tuprolog.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class TestLexerKt {
    @Test
    fun testLexing() {
        val input = "1 + a :- b"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        val tokens = lexer.getAllTokens()

        lexer.addOperators(":-")

        tokens.forEachIndexed { i, it ->
            println("token-$i=$it")
            println("type=${it.type} (i.e., ${lexer.symbolicNames[it.type]})")
            println("text=`${it.text}`")
            println("".padEnd(80, '-'))
        }

        assertEquals(5, tokens.size)
    }
}