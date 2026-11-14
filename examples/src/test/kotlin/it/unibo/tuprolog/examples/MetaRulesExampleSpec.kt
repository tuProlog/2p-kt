package it.unibo.tuprolog.examples

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.examples.solve.MetaRulesExample

private class ExpectedAnswers(
    val id: String,
    val lesson: Int,
    val query: String,
    val answers: List<String>,
)

private val expected =
    listOf(
        ExpectedAnswers("enumerate", 0, "F(X)", listOf("F = f, X = 1", "F = g, X = 2", "F = h, X = 3")),
        ExpectedAnswers("filter-by-argument", 0, "F(2)", listOf("F = g")),
        ExpectedAnswers("bound-functor", 0, "F = g, F(X)", listOf("F = g, X = 2")),
        ExpectedAnswers("virtual-predicate", 1, "parent(alice, X)", listOf("X = bob")),
        ExpectedAnswers("virtual-predicates", 1, "F(alice, X)", listOf("F = parent, X = bob", "F = likes, X = pizza")),
        ExpectedAnswers("composition", 2, "grandparent(alice, X)", listOf("X = charlie")),
        ExpectedAnswers("composed-functor", 2, "F(alice, charlie)", listOf("F = grandparent")),
        ExpectedAnswers("closure", 3, "reachable(a, X)", listOf("X = b", "X = c", "X = d")),
    )

/** The answers the metarules lesson prints are the ones it teaches. */
class MetaRulesExampleSpec :
    FunSpec({
        test("every-query-of-the-lessons-is-checked") {
            MetaRulesExample.lessons.flatMap { it.queries } shouldBe expected.map { it.query }
        }

        withTests(nameFn = { it.id }, expected) { case ->
            MetaRulesExample.answers(MetaRulesExample.lessons[case.lesson].theory, case.query) shouldBe case.answers
        }

        test("main-runs") {
            MetaRulesExample.main(emptyArray())
        }
    })
