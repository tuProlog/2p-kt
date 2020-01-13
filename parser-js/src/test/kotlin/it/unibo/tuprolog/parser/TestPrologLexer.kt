package it.unibo.tuprolog.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class TestPrologLexer {
    @Test
    fun testInitialisation() {
        val lexer = PrologLexer(null)
        assertEquals("PrologLexer.g4", lexer.grammarFileName)
    }
}