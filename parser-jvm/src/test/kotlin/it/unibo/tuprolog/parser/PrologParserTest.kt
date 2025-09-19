package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.dynamic.DynamicLexer
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.TokenStream
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrologParserTest {
    companion object {
        private fun lexerForString(input: String): PrologLexer = PrologLexer(CharStreams.fromString(input))

        private fun tokenStreamFromLexer(lexer: DynamicLexer): TokenStream = BufferedTokenStream(lexer)

        // private fun tokenStreamToList(stream: TokenStream): List<Token> {
        //     val result = LinkedList<Token>()
        //     var i = 0
        //     stream.consume()
        //     do {
        //         result.add(stream[i++])
        //         stream.consume()
        //     } while (stream.LA(1) != TokenStream.EOF)
        //     result.add(stream[i])
        //     return result
        // }

        // private fun assertTokenIs(token: Token, type: Int, text: String) {
        //     Assert.assertEquals("'$text'", "'" + token.text + "'")
        //     Assert.assertEquals(
        //         PrologLexer.VOCABULARY.getSymbolicName(type),
        //         PrologLexer.VOCABULARY.getSymbolicName(token.type)
        //     )
        // }

        private fun parseTerm(string: String): PrologParser.SingletonTermContext {
            val parser: PrologParser = this.createParser(string)
            parser.addErrorListener(
                object : BaseErrorListener() {
                    override fun syntaxError(
                        recognizer: Recognizer<*, *>,
                        offendingSymbol: Any,
                        line: Int,
                        charPositionInLine: Int,
                        msg: String,
                        e: RecognitionException,
                    ): Unit = throw e
                },
            )
            return parser.singletonTerm()
        }

        // private fun parseExpression(string: String): PrologParser.SingletonExpressionContext {
        //     val parser: PrologParser = createParser(string)
        //     parser.addErrorListener(
        //         object : BaseErrorListener() {
        //             override fun syntaxError(
        //                 recognizer: Recognizer<*, *>,
        //                 offendingSymbol: Any,
        //                 line: Int,
        //                 charPositionInLine: Int,
        //                 msg: String,
        //                 e: RecognitionException
        //             ) {
        //                 throw e
        //             }
        //         }
        //     )
        //     return parser.singletonExpression()
        // }

        private fun createParser(string: String): PrologParser =
            PrologParser(
                tokenStreamFromLexer(
                    lexerForString(
                        string,
                    ),
                ),
            )
    }

    @Test
    fun testInteger() {
        val tc = parseTerm("1").term()
        assertTrue(tc.isNum && !tc.isExpr && !tc.isList && !tc.isStruct && !tc.isVar)
        val nc = tc.number()
        assertTrue(nc.isInt && !nc.isReal)
        val ic = nc.integer()
        assertEquals(ic.value.text.toInt(), 1)
    }

    @Test
    fun testReal() {
        val tc = parseTerm("1.1").term()
        assertTrue(tc.isNum && !tc.isExpr && !tc.isList && !tc.isStruct && !tc.isVar)
        val nc = tc.number()
        assertTrue(nc.isReal && !nc.isInt)
        val rc = nc.real()
        assertEquals(rc.value.text.toDouble(), 1.1)
    }

    @Test
    fun testAtom() {
        val tc = parseTerm("a").term()
        assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
        val sc = tc.structure()
        assertTrue(
            sc.arity == sc.args.count() &&
                sc.arity == 0 &&
                !sc.isList &&
                !sc.isBlock &&
                !sc.isTruth &&
                sc.functor.text == "a" &&
                sc.functor.type == PrologLexer.ATOM,
        )
    }

    @Test
    fun testString() {
        sequenceOf("'a'", "\"a\"").forEach {
            val tc = parseTerm(it).term()
            assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
            val s = tc.structure()
            assertTrue(
                s.arity == s.args.count() &&
                    s.arity == 0 &&
                    s.isString &&
                    !s.isBlock &&
                    !s.isList &&
                    !s.isTruth &&
                    s.functor.text == "a" &&
                    (s.functor.type == PrologLexer.DQ_STRING || s.functor.type == PrologLexer.SQ_STRING),
            )
        }
    }

    @Test
    fun testTrue() {
        val tc = parseTerm("true").term()
        assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
        val s = tc.structure()
        assertTrue(
            s.arity == s.args.count() &&
                s.isTruth &&
                !s.isList &&
                !s.isString &&
                s.functor.text == "true" &&
                s.functor.type == PrologLexer.BOOL,
        )
    }

    @Test
    fun testFail() {
        val tc = parseTerm("fail").term()
        assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
        val s = tc.structure()
        assertTrue(
            s.arity == s.args.count() &&
                s.isTruth &&
                !s.isList &&
                !s.isString &&
                s.functor.text == "fail" &&
                s.functor.type == PrologLexer.BOOL,
        )
    }

    @Test
    fun testFalse() {
        val tc = parseTerm("false").term()
        assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
        val s = tc.structure()
        assertTrue(
            s.arity == s.args.count() &&
                s.isTruth &&
                !s.isList &&
                !s.isString &&
                s.functor.text == "false" &&
                s.functor.type == PrologLexer.BOOL,
        )
    }

    @Test
    fun testEmptyList() {
        sequenceOf("[]", "[ ]", "[   ]").forEach {
            val tc = parseTerm(it).term()
            assertTrue(tc.isStruct && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isVar)
            val s = tc.structure()
            assertTrue(
                s.arity == s.args.count() &&
                    s.arity == 0 &&
                    s.isList &&
                    !s.isTruth &&
                    !s.isString &&
                    s.functor.type == PrologLexer.EMPTY_LIST,
            )
        }
    }

    @Test
    fun testVar() {
        sequenceOf("A", "_A", "_1A", "A_").forEach {
            val tc = parseTerm(it).term()
            assertTrue(tc.isVar && !tc.isExpr && !tc.isList && !tc.isNum && !tc.isStruct)
            val v = tc.variable()
            assertTrue(
                !v.isAnonymous &&
                    v.value.text.contains("A") &&
                    v.value.type == PrologLexer.VARIABLE,
            )
        }
    }

    @Suppress("CyclomaticComplexMethod")
    @Test
    fun testSingletonList() {
        sequenceOf("[1]", "[1 ]", "[ 1]", "[ 1 ]").forEach {
            val tc = parseTerm(it).term()
            assertTrue(tc.isList && !tc.isExpr && !tc.isVar && !tc.isNum && !tc.isStruct)
            val l = tc.list()
            assertTrue(
                l.length == l.items.count() &&
                    l.length == 1 &&
                    !l.hasTail &&
                    l.tail == null,
            )
            val expr = l.items[0]
            assertTrue(expr.isTerm && expr.left != null && expr.operators.isEmpty() && expr.right.isEmpty())
            val t = expr.left!!
            assertTrue(t.isNum && !t.isVar && !t.isList && !t.isStruct && !t.isExpr)
            val n = t.number()
            assertTrue(n.isInt && !n.isReal)
            val i = n.integer()
            assertEquals(i.value.text.toInt(), 1)
        }
    }
}
