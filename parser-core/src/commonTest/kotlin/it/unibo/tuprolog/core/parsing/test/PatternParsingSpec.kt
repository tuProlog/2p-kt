package it.unibo.tuprolog.core.parsing.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.TermParser

private val parser = TermParser.withDefaultOperators()

/** Inputs which parse, then format back exactly, with readable formatting. */
private val roundTrips =
    listOf(
        "P(X)",
        "P(X, Y)",
        "f(P(a), b)",
        "P(Q(X))",
        "P(X) :- Q(X)",
        "P(A, B) :- Q(A, C), R(C, B)",
        "p(X) :- Q(X), \\+ R(X)",
        "[P(a), Q(b)]",
    )

class PatternParsingSpec :
    FunSpec({
        test("variable-with-arguments-is-a-pattern") {
            val term = parser.parseTerm("P(X, a)").shouldBeInstanceOf<Pattern>()
            term.functor.name shouldBe "P"
            term.arity shouldBe 2
        }

        test("variable-alone-is-a-variable") {
            parser.parseTerm("P").shouldBeInstanceOf<Var>()
            parser.parseTerm("p(X)").shouldBeInstanceOf<Struct>()
        }

        test("empty-arguments-are-rejected") {
            shouldThrow<ParseException> { parser.parseTerm("P()") }
        }

        test("chain-metarule-is-a-rule-with-pattern-head-and-body") {
            val rule = parser.parseClause("P(A, B) :- Q(A, C), R(C, B)").shouldBeInstanceOf<Rule>()
            rule.head.shouldBeInstanceOf<Pattern>()
            rule.bodyItems.forEach { it.shouldBeInstanceOf<Pattern>() }
        }

        test("variables-are-shared-across-the-clause") {
            val rule = parser.parseClause("P(X) :- f(P), Q(X)")
            val head = rule.head.shouldBeInstanceOf<Pattern>()
            val first = rule.getBodyItem(0).castToStruct()
            val second = rule.getBodyItem(1).castToPattern()
            first[0] shouldBeSameInstanceAs head.functor
            second[0] shouldBeSameInstanceAs head[0]
        }

        withTests(nameFn = { "round-trip/$it" }, roundTrips) { text ->
            TermFormatter.readable().format(parser.parseTerm(text)) shouldBe text
        }

        test("indicator-with-variable-name") {
            val indicator = parser.parseTerm("P/2").castToIndicator()
            indicator.isWellFormed shouldBe true
            indicator.isConcrete shouldBe false
        }
    })
