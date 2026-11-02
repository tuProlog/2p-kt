package it.unibo.tuprolog.core.parsing.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.InvalidTermTypeException
import it.unibo.tuprolog.core.parsing.TermParser

class PublicTermParserContractSpec :
    FunSpec({
        val parser = TermParser.withNoOperator()

        test("typed-entry-points-accept-matching-terms") {
            parser.parseAtom("atom").shouldBeInstanceOf<Atom>()
            parser.parseVar("Variable").shouldBeInstanceOf<Var>()
            parser.parseInteger("42").shouldBeInstanceOf<Integer>()
            parser.parseReal("3.25").shouldBeInstanceOf<Real>()
            parser.parseStruct("f(a)").shouldBeInstanceOf<Struct>()
        }

        test("typed-entry-points-reject-mismatching-terms-with-structured-error") {
            val error = shouldThrow<InvalidTermTypeException> { parser.parseInteger("atom") }
            error.input shouldBe "atom"
            error.term.shouldBeInstanceOf<Atom>()
            error.type shouldBe Integer::class
        }

        test("parser-factories-expose-the-requested-default-operator-set") {
            TermParser.withNoOperator().defaultOperatorSet shouldBe OperatorSet.EMPTY
            TermParser.withStandardOperators().defaultOperatorSet shouldBe OperatorSet.STANDARD
            TermParser.withDefaultOperators().defaultOperatorSet shouldBe OperatorSet.DEFAULT
        }

        test("anonymous-variables-in-the-same-term-are-distinct") {
            val variables = parser.parseTerm("f(_, _)").variables.toList()

            variables.size shouldBe 2
            variables.all(Var::isAnonymous).shouldBeTrue()
            variables[1] shouldNotBe variables[0]
        }

        test("anonymous-variables-in-the-same-clause-are-distinct") {
            val variables =
                TermParser
                    .withStandardOperators()
                    .parseClause("f(_, _) :- true")
                    .variables
                    .toList()

            variables.size shouldBe 2
            variables.all(Var::isAnonymous).shouldBeTrue()
            variables[1] shouldNotBe variables[0]
        }
    })
