package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.InvalidTermTypeException
import it.unibo.tuprolog.core.parsing.TermParser
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PublicTermParserContractSpec :
    FunSpec({
        val parser = TermParser.withNoOperator()

        test("typed-entry-points-accept-matching-terms") {
            assertIs<Atom>(parser.parseAtom("atom"))
            assertIs<Var>(parser.parseVar("Variable"))
            assertIs<Integer>(parser.parseInteger("42"))
            assertIs<Real>(parser.parseReal("3.25"))
            assertIs<Struct>(parser.parseStruct("f(a)"))
        }

        test("typed-entry-points-reject-mismatching-terms-with-structured-error") {
            val error = assertFailsWith<InvalidTermTypeException> { parser.parseInteger("atom") }
            assertEquals("atom", error.input)
            assertIs<Atom>(error.term)
            assertEquals(Integer::class, error.type)
        }

        test("parser-factories-expose-the-requested-default-operator-set") {
            assertEquals(OperatorSet.EMPTY, TermParser.withNoOperator().defaultOperatorSet)
            assertEquals(OperatorSet.STANDARD, TermParser.withStandardOperators().defaultOperatorSet)
            assertEquals(OperatorSet.DEFAULT, TermParser.withDefaultOperators().defaultOperatorSet)
        }

        test("anonymous-variables-in-the-same-term-are-distinct") {
            val variables = parser.parseTerm("f(_, _)").variables.toList()

            assertEquals(2, variables.size)
            assertTrue(variables.all(Var::isAnonymous))
            assertNotEquals(variables[0], variables[1])
        }

        test("anonymous-variables-in-the-same-clause-are-distinct") {
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
    })
