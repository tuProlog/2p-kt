package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.InvalidTermTypeException
import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PublicTermParserContractTest {
    private val parser = TermParser.withNoOperator()

    @Test
    fun typedEntryPointsAcceptMatchingTerms() {
        assertIs<Atom>(parser.parseAtom("atom"))
        assertIs<Var>(parser.parseVar("Variable"))
        assertIs<Integer>(parser.parseInteger("42"))
        assertIs<Real>(parser.parseReal("3.25"))
        assertIs<Struct>(parser.parseStruct("f(a)"))
    }

    @Test
    fun typedEntryPointsRejectMismatchingTermsWithStructuredError() {
        val error = assertFailsWith<InvalidTermTypeException> { parser.parseInteger("atom") }
        assertEquals("atom", error.input)
        assertIs<Atom>(error.term)
        assertEquals(Integer::class, error.type)
    }

    @Test
    fun parserFactoriesExposeTheRequestedDefaultOperatorSet() {
        assertEquals(OperatorSet.EMPTY, TermParser.withNoOperator().defaultOperatorSet)
        assertEquals(OperatorSet.STANDARD, TermParser.withStandardOperators().defaultOperatorSet)
        assertEquals(OperatorSet.DEFAULT, TermParser.withDefaultOperators().defaultOperatorSet)
    }

    @Test
    fun anonymousVariablesInTheSameTermAreDistinct() {
        val variables = parser.parseTerm("f(_, _)").variables.toList()

        assertEquals(2, variables.size)
        assertTrue(variables.all(Var::isAnonymous))
        assertNotEquals(variables[0], variables[1])
    }

    @Test
    fun anonymousVariablesInTheSameClauseAreDistinct() {
        val variables =
            TermParser
                .withStandardOperators()
                .parseClause("f(_, _) :- true")
                .variables
                .toList()

        assertEquals(2, variables.size)
        assertTrue(variables.all(Var::isAnonymous))
        assertNotEquals(variables[0], variables[1])
    }
}
