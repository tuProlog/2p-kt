package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.PrologParser
import it.unibo.tuprolog.parser.dynamic.DynamicLexer
import org.antlr.v4.runtime.*
import org.junit.Assert
import org.junit.Test
import java.util.*

class PrologParserTest {

    class AssertionOn<T>(private val `object`: T) {
        fun <U> andThenAssertUnit(
            getter: (T) -> U,
            asserter: (U) -> Unit
        ): AssertionOn<U> {
            val property = getter(`object`)
            asserter(property)
            return AssertionOn(property)
        }

        fun <U> andThenAssertBool(
            getter: (T) -> U,
            asserter: (U) -> Boolean
        ): AssertionOn<U> {
            val property = getter(`object`)
            asserter(property)
            return AssertionOn(property)
        }

    }
    companion object {

        private fun lexerForString(input: String): PrologLexer {
            return PrologLexer(ANTLRInputStream(input))
        }

        private fun tokenStreamFromLexer(lexer: DynamicLexer): TokenStream {
            return BufferedTokenStream(lexer)
        }

        private fun tokenStreamToList(stream: TokenStream): List<Token> {
            val result = LinkedList<Token>()
            var i = 0
            stream.consume()
            do {
                result.add(stream[i++])
                stream.consume()
            } while (stream.LA(1) != TokenStream.EOF)
            result.add(stream[i])
            return result
        }

        private fun assertTokenIs(token: Token, type: Int, text: String) {
            Assert.assertEquals("'$text'", "'" + token.text + "'")
            Assert.assertEquals(
                PrologLexer.VOCABULARY.getSymbolicName(type),
                PrologLexer.VOCABULARY.getSymbolicName(token.type)
            )
        }

        private fun parseTerm(string: String): PrologParser.SingletonTermContext {
            val parser: PrologParser = this.createParser(string)
            parser.addErrorListener(object : BaseErrorListener() {
                override fun syntaxError(
                    recognizer: Recognizer<*, *>,
                    offendingSymbol: Any,
                    line: Int,
                    charPositionInLine: Int,
                    msg: String,
                    e: RecognitionException
                ) {
                    throw e
                }
            })
            return parser.singletonTerm()
        }

        private fun parseExpression(string: String): PrologParser.SingletonExpressionContext {
            val parser: PrologParser = createParser(string)
            parser.addErrorListener(object : BaseErrorListener() {
                override fun syntaxError(
                    recognizer: Recognizer<*, *>,
                    offendingSymbol: Any,
                    line: Int,
                    charPositionInLine: Int,
                    msg: String,
                    e: RecognitionException
                ) {
                    throw e
                }
            })
            return parser.singletonExpression()
        }

        private fun createParser(string: String): PrologParser {
            return PrologParser(
                tokenStreamFromLexer(
                    lexerForString(
                        string
                    )
                )
            )
        }

        private fun <T, U> assertionOnUnit(
            `object`: T,
            getter: (T) -> U,
            asserter: (U) -> Unit
        ): AssertionOn<U> {
            return AssertionOn(`object`).andThenAssertUnit(getter, asserter)
        }

        private fun <T, U> assertionOnBool(
            `object`: T,
            getter: (T) -> U,
            asserter: (U) -> Boolean
        ): AssertionOn<U> {
            return AssertionOn(`object`).andThenAssertBool(getter, asserter)
        }

    }

    @Test
    fun testInteger(){
        assertionOnBool(
            parseTerm("1"),
            PrologParser.SingletonTermContext::term,
            {
                it.isNum&&!it.isExpr&&!it.isList&&!it.isStruct&&!it.isVar
            }
        ).andThenAssertBool(
            PrologParser.TermContext::number)
         {
            it.isInt && !it.isReal
        }.andThenAssertBool(
            PrologParser.NumberContext::integer
        ) {
            it.value.text.toInt() == 1
        }
    }

    @Test
    fun testReal(){
        assertionOnBool(
            parseTerm("1.1"),
            PrologParser.SingletonTermContext::term,
            {
                it.isNum && !it.isExpr && !it.isList && !it.isStruct && !it.isVar
            }
        ).andThenAssertBool(
            PrologParser.TermContext::number)
        {
            it.isReal && !it.isInt
        }.andThenAssertBool(
            PrologParser.NumberContext::real
        ) {
            it.value.text.toDouble() == 1.1
        }
    }

    @Test
    fun testAtom(){
        assertionOnBool(
            parseTerm("a"),
            PrologParser.SingletonTermContext::term,
            {
                it.isStruct && !it.isNum && !it.isExpr && !it.isList && !it.isVar
            }
        ).andThenAssertBool(
            PrologParser.TermContext::structure)
        {
            it.arity == it.args.count() &&
                    it.arity == 0 &&
                    !it.isList &&
                    !it.isSet &&
                    !it.isTruth &&
                    it.functor.text == "a" &&
                    it.functor.type == PrologLexer.ATOM
        }
    }

    @Test
    fun testString(){
        sequenceOf("'a'","\"a\"").forEach {
            assertionOnBool(
                parseTerm(it),PrologParser.SingletonTermContext::term)
                {
                   t -> t.isStruct && !t.isNum && !t.isExpr && !t.isList && !t.isVar
                }.andThenAssertBool(
                PrologParser.TermContext::structure){
                      s -> s.arity == s.args.count() &&
                        s.arity == 0 &&
                        s.isSet && !s.isList && !s.isTruth &&
                        s.functor.text == "a" &&
                    (s.functor.type == PrologLexer.DQ_STRING || s.functor.type == PrologLexer.SQ_STRING)
            }
        }
    }


}