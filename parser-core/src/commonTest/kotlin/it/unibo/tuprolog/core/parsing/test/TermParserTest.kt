package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.Test

class TermParserTest {

    @Test
    fun testParsingWithCanonicalTerms() {
        val parser = TermParser.withNoOperator

        ParsingExamples.canonicalTerms.forEach {
            parser.assertTermIsCorrectlyParsed(it.first, it.second)
        }
    }

    @Test
    fun testParsingWithStandardOperators() {
        val parser = TermParser.withStandardOperators

        ParsingExamples.expressions.forEach {
            parser.assertTermIsCorrectlyParsed(it.first, it.second)
        }
    }

}