package it.unibo.tuprolog.core.parsing.test

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.parsing.TermParser

class StandardOperatorTermParserSpec :
    FunSpec({
        val parser = TermParser.withStandardOperators()

        val cases = ParsingExamples.expressions.toList()

        fun assertCase(index: Int) = parser.assertTermIsCorrectlyParsed(cases[index].first, cases[index].second)

        test("nested-struct-argument") {
            assertCase(0)
        }

        test("parenthesized-conjunction-argument") {
            assertCase(1)
        }

        test("simple-addition") {
            assertCase(2)
        }

        test("arithmetic-expression-with-precedence") {
            assertCase(3)
        }

        test("disjunction-of-variables") {
            assertCase(4)
        }

        test("clause-with-disjunction-head") {
            assertCase(5)
        }

        test("clause-with-disjunction-body") {
            assertCase(6)
        }

        test("clause-with-disjunction-head-and-conjunction-body") {
            assertCase(7)
        }

        test("clause-with-conjunction-in-head-disjunction-and-conjunction-body") {
            assertCase(8)
        }

        test("clause-with-conjunction-in-both-head-and-body-disjunctions") {
            assertCase(9)
        }

        test("clause-with-lists-in-head-and-conjunctions-in-body") {
            assertCase(10)
        }

        test("clause-with-if-then-else-in-head-and-disjunction-in-body") {
            assertCase(11)
        }

        test("fact-with-list-argument") {
            assertCase(12)
        }

        test("clause-with-list-destructuring-in-head") {
            assertCase(13)
        }

        test("fact-with-list-pattern-argument") {
            assertCase(14)
        }

        test("recursive-clause-with-list-destructuring") {
            assertCase(15)
        }

        test("clause-with-comparison-and-arithmetic-in-body") {
            assertCase(16)
        }

        test("base-fact-with-empty-list-and-zero") {
            assertCase(17)
        }

        test("recursive-clause-computing-length") {
            assertCase(18)
        }

        test("clause-delegating-to-helper-predicate") {
            assertCase(19)
        }

        test("base-clause-with-cut") {
            assertCase(20)
        }

        test("recursive-clause-accumulating-reversed-list") {
            assertCase(21)
        }

        test("clause-calling-builtin-reverse") {
            assertCase(22)
        }

        test("clause-with-negation-as-failure") {
            assertCase(23)
        }

        test("base-fact-with-two-empty-lists") {
            assertCase(24)
        }

        test("recursive-clause-flattening-nested-lists") {
            assertCase(25)
        }

        test("clause-with-inequality-and-recursive-call") {
            assertCase(26)
        }

        test("clause-with-comparison-over-base-case") {
            assertCase(27)
        }

        test("clause-with-inequality-over-base-case") {
            assertCase(28)
        }

        test("clause-with-comparison-and-inequality-conjunction") {
            assertCase(29)
        }

        test("clause-with-arithmetic-and-recursive-call") {
            assertCase(30)
        }

        test("clause-computing-upper-bounds-from-map-size") {
            assertCase(31)
        }

        test("clause-with-nested-comparisons-and-conjunctions") {
            assertCase(32)
        }

        test("clause-with-negated-call-in-body") {
            assertCase(33)
        }

        test("clause-with-multiple-goals-and-arithmetic-update") {
            assertCase(34)
        }

        test("if-then-else-with-conjunction-and-format-call") {
            assertCase(35)
        }

        test("clause-with-deeply-nested-conjunctions-and-arithmetic") {
            assertCase(36)
        }
    })
