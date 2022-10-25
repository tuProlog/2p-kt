package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.Test
import kotlin.test.assertEquals

class TermParserTest {

    @Test
    fun testParsingWithCanonicalTerms() {
        val parser = TermParser.withNoOperator()

        ParsingExamples.canonicalTerms.forEach {
            parser.assertTermIsCorrectlyParsed(it.first, it.second)
        }
    }

    @Test
    fun testParsingWithStandardOperators() {
        val parser = TermParser.withStandardOperators()

        ParsingExamples.expressions.forEach {
            parser.assertTermIsCorrectlyParsed(it.first, it.second)
        }
    }

    @Test
    fun testStreamParsingWithCanonicalTerm() {
        val parser = TermParser.withNoOperator()

        val input = ParsingExamples.canonicalTerms.map { it.first }.joinToString(" ")

        val parsed = parser.parseTerms(input).toList()

        assertEquals(ParsingExamples.canonicalTerms.count(), parsed.size)

        ParsingExamples.canonicalTerms.zip(parsed.asSequence())
            .map { (xy, z) -> Triple(xy.first, xy.second, z) }
            .forEach { (_, expected, parsed) ->
                assertTermsAreEqual(expected, parsed)
            }
    }
}
