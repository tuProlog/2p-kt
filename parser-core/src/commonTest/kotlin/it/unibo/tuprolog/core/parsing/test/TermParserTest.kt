package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.core.parsing.withNoOperator
import it.unibo.tuprolog.core.parsing.withStandardOperators
import it.unibo.tuprolog.core.toTerm
import kotlin.test.Test

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

    @Test fun testProva(){
        val parser = TermParser.withNoOperator()
        parser.assertTermIsCorrectlyParsed("a", "a".toTerm())
        parser.assertTermIsCorrectlyParsed("A","A".toTerm())
        parser.assertTermIsCorrectlyParsed("_","_".toTerm())
    }

}