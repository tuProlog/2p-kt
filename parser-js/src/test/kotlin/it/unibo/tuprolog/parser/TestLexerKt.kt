package it.unibo.tuprolog.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class TestLexerKt {

    private inline val loggingOn get() = false

    @Test
    fun testLexing() {
        val input = "1 + a :- b"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)

        lexer.addOperators(":-")
        assertEquals(listOf(":-"), lexer.getOperators().toList())

        val tokens = lexer.getAllTokens()

        if (loggingOn) tokens.forEachIndexed { i, it ->
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

    @Test
    fun testAtoms() {
        val input = "1 + a + \"b\" + 'c' - \"d\""
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        val tokens = lexer.getAllTokens()

        assertEquals(9, tokens.size)

        tokens[0].let {
            assertEquals("1", it.text)
            assertEquals(PrologLexer.INTEGER, it.type)
        }

        tokens[1].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[2].let {
            assertEquals("a", it.text)
            assertEquals(PrologLexer.ATOM, it.type)
        }
        tokens[3].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[4].let {
            assertEquals("b", it.text)
            assertEquals(PrologLexer.DQ_STRING, it.type)
        }
        tokens[5].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[6].let {
            assertEquals("c", it.text)
            assertEquals(PrologLexer.SQ_STRING, it.type)
        }
        tokens[7].let {
            assertEquals("-", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[8].let {
            assertEquals("d", it.text)
            assertEquals(PrologLexer.DQ_STRING, it.type)
        }
    }

    @Test
    fun testVariables() {
        val input = "_ + A + _B is _1 + _a + _+"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        lexer.addOperators("+", "is")
        val tokens = lexer.getAllTokens()

        assertEquals(12, tokens.size)

        tokens[0].let {
            assertEquals("_", it.text)
            assertEquals(PrologLexer.VARIABLE, it.type)
        }

        tokens[1].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[2].let {
            assertEquals("A", it.text)
            assertEquals(PrologLexer.VARIABLE, it.type)
        }
        tokens[3].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[4].let {
            assertEquals("_B", it.text)
            assertEquals(PrologLexer.VARIABLE, it.type)
        }
        tokens[5].let {
            assertEquals("is", it.text)
            assertEquals(PrologLexer.OPERATOR, it.type)
        }
        tokens[6].let {
            assertEquals("_1", it.text)
            assertEquals(PrologLexer.VARIABLE, it.type)
        }
        tokens[7].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[8].let {
            assertEquals("_a", it.text)
            assertEquals(PrologLexer.VARIABLE, it.type)
        }
        tokens[9].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[10].let {
            assertEquals("_", it.text)
            assertEquals(PrologLexer.VARIABLE, it.type)
        }
        tokens[11].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
    }

    @Test
    fun testOperators() {
        val input = "1 ? a is c :- d"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        lexer.addOperators("?", "is", ":-")
        val tokens = lexer.getAllTokens()

        assertEquals(7, tokens.size)

        tokens[0].let {
            assertEquals("1", it.text)
            assertEquals(PrologLexer.INTEGER, it.type)
        }

        tokens[1].let {
            assertEquals("?", it.text)
            assertEquals(PrologLexer.OPERATOR, it.type)
        }
        tokens[2].let {
            assertEquals("a", it.text)
            assertEquals(PrologLexer.ATOM, it.type)
        }
        tokens[3].let {
            assertEquals("is", it.text)
            assertEquals(PrologLexer.OPERATOR, it.type)
        }
        tokens[4].let {
            assertEquals("c", it.text)
            assertEquals(PrologLexer.ATOM, it.type)
        }
        tokens[5].let {
            assertEquals(":-", it.text)
            assertEquals(PrologLexer.OPERATOR, it.type)
        }
        tokens[6].let {
            assertEquals("d", it.text)
            assertEquals(PrologLexer.ATOM, it.type)
        }
    }

    @Test
    fun testHex() {
        val input = "0x29 + 3 + 0xAF"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        val tokens = lexer.getAllTokens()

        assertEquals(5, tokens.size)

        tokens[0].let {
            assertEquals("0x29", it.text)
            assertEquals(PrologLexer.HEX, it.type)
        }
        tokens[1].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[2].let {
            assertEquals("3", it.text)
            assertEquals(PrologLexer.INTEGER, it.type)
        }
        tokens[3].let {
            assertEquals("+", it.text)
            assertEquals(PrologLexer.SIGN, it.type)
        }
        tokens[4].let {
            assertEquals("0xAF", it.text)
            assertEquals(PrologLexer.HEX, it.type)
        }
    }
}
