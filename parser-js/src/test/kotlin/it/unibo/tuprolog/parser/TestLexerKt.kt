package it.unibo.tuprolog.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class TestLexerKt {
    @Test
    fun testLexing() {
        val input = "1 + a :- b"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)

        lexer.addOperators(":-")
        assertEquals(listOf(":-"), lexer.getOperators().toList())

        val tokens = lexer.getAllTokens()

        tokens.forEachIndexed { i, it ->
            println("token-$i=$it")
            println("type=${it.type} (i.e., ${it.getNameAccordingTo(lexer)})")
            println("text=`${it.text}`")
            println("".padEnd(80, '-'))
        }

        assertEquals(5, tokens.size)

        tokens[0].let {
            assertEquals(PrologLexer.INTEGER, it.type)
            assertEquals("1", it.text)
        }

        tokens[1].let {
            assertEquals(PrologLexer.SIGN, it.type)
            assertEquals("+", it.text)
        }

        tokens[2].let {
            assertEquals(PrologLexer.ATOM, it.type)
            assertEquals("a", it.text)
        }

        tokens[3].let {
            assertEquals(PrologLexer.OPERATOR, it.type)
            assertEquals(":-", it.text)
        }

        tokens[4].let {
            assertEquals(PrologLexer.ATOM, it.type)
            assertEquals("b", it.text)
        }
    }
}