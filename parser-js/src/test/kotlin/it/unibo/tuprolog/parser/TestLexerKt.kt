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

    @Test
    fun testAtoms() {
        val input = "1 + a + \"b\" + 'c' - \"d\""
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        val tokens = lexer.getAllTokens()

        assertEquals(tokens[0].text,"1")
        assertEquals(tokens[1].text,"+")
        assertEquals(tokens[2].text,"a")
        assertEquals(tokens[3].text,"+")
        assertEquals(tokens[4].text,"b")
        assertEquals(tokens[5].text,"+")
        assertEquals(tokens[6].text,"c")
        assertEquals(tokens[7].text, "-")
        assertEquals(tokens[8].text,"d")

        assertEquals(tokens[0].type,2)
        assertEquals(tokens[1].type,6)
        assertEquals(tokens[2].type,28)
        assertEquals(tokens[3].type,6)
        assertEquals(tokens[4].type,19)
        assertEquals(tokens[5].type,6)
        assertEquals(tokens[6].type,18)
        assertEquals(tokens[7].type,6)
        assertEquals(tokens[8].type,19)
    }

    @Test
    fun testVariables(){
        val input = "_ + A + _B is _1 + _a + _+"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        lexer.addOperators("+","is")
        val tokens = lexer.getAllTokens()

        assertEquals(tokens[0].text,"_")
        assertEquals(tokens[1].text,"+")
        assertEquals(tokens[2].text,"A")
        assertEquals(tokens[3].text,"+")
        assertEquals(tokens[4].text,"_B")
        assertEquals(tokens[5].text,"is")
        assertEquals(tokens[6].text,"_1")
        assertEquals(tokens[7].text, "+")
        assertEquals(tokens[8].text,"_a")
        assertEquals(tokens[9].text,"+")
        assertEquals(tokens[10].text,"_")
        assertEquals(tokens[11].text,"+")

        assertEquals(tokens[0].type,1)
        assertEquals(tokens[1].type,6)
        assertEquals(tokens[2].type,1)
        assertEquals(tokens[3].type,6)
        assertEquals(tokens[4].type,1)
        assertEquals(tokens[5].type,27)
        assertEquals(tokens[6].type,1)
        assertEquals(tokens[7].type,6)
        assertEquals(tokens[8].type,1)
        assertEquals(tokens[9].type,6)
        assertEquals(tokens[10].type,1)
        assertEquals(tokens[11].type,6)
    }

    @Test
    fun testOperators(){
        val input = "1 ? a is c :- d"
        val inputStream = InputStream(input)
        val lexer = PrologLexer(inputStream)
        lexer.addOperators("?","is", ":-")
        val tokens = lexer.getAllTokens()

        assertEquals(tokens[0].text,"1")
        assertEquals(tokens[1].text,"?")
        assertEquals(tokens[2].text,"a")
        assertEquals(tokens[3].text,"is")
        assertEquals(tokens[4].text,"c")
        assertEquals(tokens[5].text,":-")
        assertEquals(tokens[6].text,"d")

        assertEquals(tokens[0].type,2)
        assertEquals(tokens[1].type,27)
        assertEquals(tokens[2].type,28)
        assertEquals(tokens[3].type,27)
        assertEquals(tokens[4].type,28)
        assertEquals(tokens[5].type,27)
        assertEquals(tokens[6].type,28)
    }
}