package it.unibo.tuprolog.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestPrologLexer {
    @Test
    fun testInitialisation() {
        val lexer = PrologLexer(null)
        assertEquals("PrologLexer.g4", lexer.grammarFileName)
        val modeNames = arrayOf("DEFAULT_MODE")
        lexer.modeNames.forEachIndexed { index, mode ->
            assertEquals(modeNames[index], mode)
        }
        val channelNames = arrayOf("DEFAULT_TOKEN_CHANNEL", "HIDDEN")
        lexer.channelNames.forEachIndexed { index, channel ->
            assertEquals(channelNames[index], channel)
        }
        val ruleNames = arrayOf(
            "INTEGER", "HEX", "OCT", "BINARY", "SIGN",
            "FLOAT", "CHAR", "BOOL", "LPAR", "RPAR",
            "LSQUARE", "RSQUARE", "EMPTY_LIST",
            "LBRACE", "RBRACE", "EMPTY_SET", "VARIABLE",
            "SQ_STRING", "DQ_STRING", "COMMA", "PIPE",
            "CUT", "FULL_STOP", "FullStopTerminator",
            "WHITE_SPACES", "COMMENT", "LINE_COMMENT",
            "OPERATOR", "ATOM", "Symbols", "Escapable",
            "DoubleSQ", "DoubleDQ", "OpSymbol",
            "Atom", "Ws", "OctDigit", "BinDigit",
            "HexDigit", "Digit", "Zero"
        )
        lexer.ruleNames.forEachIndexed { index, rule ->
            assertEquals(ruleNames[index], rule)
        }
    }

    @Test
    fun testLexerRecognizeOperators() {
        val lexer = PrologLexer("a ; b :- c")
        val ops = listOf(";", ":-", "+")
        lexer.addOperators(";", ":-", "+")
        lexer.getOperators().forEachIndexed { index, op ->
            assertEquals(ops[index], op)
        }
        assertTrue(lexer.isOperator(";"))
        assertFalse(lexer.isOperator("jj"))
        assertTrue(lexer.isOperator(":-"))
        assertFalse(lexer.isOperator("+a"))
    }

    @Test
    fun testLexerUnquote() {
        val lexer = PrologLexer(null)
        assertEquals("String", lexer.unquote("'String'"))
        assertEquals("sec''sds", lexer.unquote("'sec''sds'"))
    }

    @Test
    fun testLexerEscape() {
        val lexer = PrologLexer(null)
        lexer.escape("first\\nsec\\rthird", StringType.SINGLE_QUOTED)
        assertEquals("first\nsec\rthird", lexer.escape("first\\nsec\\rthird", StringType.SINGLE_QUOTED))
    }
}
