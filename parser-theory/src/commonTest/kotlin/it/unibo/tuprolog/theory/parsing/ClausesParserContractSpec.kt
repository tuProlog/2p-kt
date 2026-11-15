package it.unibo.tuprolog.theory.parsing

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

class ClausesParserContractSpec :
    FunSpec({
        fun Iterable<Clause>.functors(): List<String> = map { (it as Fact).head.castToStruct().functor }

        fun Sequence<Clause>.functors(): List<String> = map { (it as Fact).head.castToStruct().functor }.toList()

        fun Iterable<Clause>.singleHead(): Struct = (single() as Fact).head.castToStruct()

        fun Sequence<Clause>.singleHead(): Struct = (single() as Fact).head.castToStruct()

        fun Theory.singleHead(): Struct = (single() as Fact).head.castToStruct()

        test("factories-expose-the-requested-default-operators") {
            val custom = Operator("++", Specifier.YFX, 500)

            ClausesParser.withNoOperator().defaultOperatorSet shouldBe OperatorSet.EMPTY
            ClausesParser.withStandardOperators().defaultOperatorSet shouldBe OperatorSet.STANDARD
            ClausesParser.withDefaultOperators().defaultOperatorSet shouldBe OperatorSet.DEFAULT
            ClausesParser.withOperators(OperatorSet(custom)).defaultOperatorSet shouldBe OperatorSet(custom)
            ClausesParser.withOperators(custom).defaultOperatorSet shouldBe OperatorSet(custom)
        }

        test("eager-and-lazy-entry-points-preserve-clause-order") {
            val parser = ClausesParser.withNoOperator()
            val input = "first. second(X). third."

            parser.parseClauses(input).functors() shouldBe listOf("first", "second", "third")
            parser.parseClausesLazily(input).functors() shouldBe listOf("first", "second", "third")
        }

        test("explicit-operators-override-the-parser-default") {
            val custom = Operator("++", Specifier.YFX, 500)
            val clauses = ClausesParser.withNoOperator().parseClauses("a ++ b.", OperatorSet(custom))
            val head = (clauses.single() as Fact).head

            head.functor shouldBe "++"
            (head.args.map { (it as Struct).functor }) shouldBe listOf("a", "b")
        }

        test("theory-entry-points-use-the-requested-unificator") {
            val parser = ClausesParser.withNoOperator()
            val unificator = Unificator.naive()

            parser.parseTheory("a.", OperatorSet.EMPTY, unificator).unificator shouldBeSameInstanceAs unificator
            parser.parseTheory("a.", unificator).unificator shouldBeSameInstanceAs unificator
            parser.parseTheory("a.", OperatorSet.EMPTY).unificator shouldBeSameInstanceAs Unificator.default
            parser.parseTheory("a.").unificator shouldBeSameInstanceAs Unificator.default
        }

        test("companion-and-string-extensions-cover-default-and-explicit-operators") {
            val custom = OperatorSet(Operator("++", Specifier.YFX, 500))

            Theory.parse("a.").size shouldBe 1L
            Theory.parse("a ++ b.", custom).singleHead().functor shouldBe "++"
            "a.".parseAsTheory().size shouldBe 1L
            "a ++ b.".parseAsTheory(custom).singleHead().functor shouldBe "++"
            "a.".parseAsClauses().singleHead().functor shouldBe "a"
            "a ++ b.".parseAsClauses(custom).singleHead().functor shouldBe "++"
            "a.".parseAsClausesLazily().singleHead().functor shouldBe "a"
            "a ++ b.".parseAsClausesLazily(custom).singleHead().functor shouldBe "++"
        }

        withTests(
            nameFn = { "empty-and-trivia-only-inputs-produce-empty-results/${it.id}" },
            listOf(
                TriviaCase("empty", ""),
                TriviaCase("whitespace", "  \n\t"),
                TriviaCase("line-comment", "% comment without a newline"),
                TriviaCase("block-comment", "/* block comment */"),
            ),
        ) { case ->
            val parser = ClausesParser.withDefaultOperators()

            withClue(case.input) { parser.parseClauses(case.input).isEmpty().shouldBeTrue() }
            withClue(case.input) { parser.parseTheory(case.input).isEmpty.shouldBeTrue() }
        }

        test("anonymous-variables-in-the-same-parsed-clause-are-distinct") {
            val variables =
                ClausesParser
                    .withDefaultOperators()
                    .parseClauses("f(_, _).")
                    .single()
                    .variables
                    .toList()

            variables.size shouldBe 2
            variables.all(Var::isAnonymous).shouldBeTrue()
            variables[1] shouldNotBe variables[0]
        }

        test("variables-in-repeated-non-ground-theory-clauses-are-distinct") {
            val clauses =
                ClausesParser
                    .withDefaultOperators()
                    .parseTheory("f(X, Y).\nf(X, Y).")
                    .toList()

            clauses.size shouldBe 2
            clauses[0].variables.toSet().size shouldBe 2
            clauses[1].variables.toSet().size shouldBe 2
            (
                clauses[0]
                    .variables
                    .toSet()
                    .intersect(clauses[1].variables.toSet())
                    .isEmpty()
            ).shouldBeTrue()
        }
    })

private class TriviaCase(
    val id: String,
    val input: String,
)
