package it.unibo.tuprolog.ui.web

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlin.time.Duration.Companion.microseconds

class WebIdeSolutionLineSpec :
    FunSpec({
        test("a yes solution with a solved query is rendered with it") {
            val solution = SolutionPresentation.Yes(query = "member(X, [a]).", solvedQuery = "member(a, [a])")

            solutionLineText(1, solution) shouldBe "1. yes: member(a, [a])"
        }

        test("a yes solution with bindings appends them, one per line") {
            val solution =
                SolutionPresentation.Yes(
                    query = "member(X, [a]).",
                    bindings = listOf(BindingPresentation("X", "a")),
                    solvedQuery = "member(a, [a])",
                )

            solutionLineText(1, solution) shouldBe "1. yes: member(a, [a])\n  X = a"
        }

        test("a null solvedQuery is rendered as a bare yes, for GroundQueriesHaveBooleanSolution") {
            val solution = SolutionPresentation.Yes(query = "2 is 1 + 1.", solvedQuery = null)

            solutionLineText(1, solution) shouldBe "1. yes."
        }

        test("a no solution is rendered as no") {
            solutionLineText(2, SolutionPresentation.No("fail.")) shouldBe "2. no"
        }

        test("a timed solution shows its time before the outcome") {
            val solution =
                SolutionPresentation.Yes(
                    query = "member(X, [a]).",
                    bindings = listOf(BindingPresentation("X", "a")),
                    solvedQuery = "member(a, [a])",
                    time = 1234.microseconds,
                )

            solutionLineText(1, solution) shouldBe "1. [1.234ms] yes: member(a, [a])\n  X = a"
        }
    })
