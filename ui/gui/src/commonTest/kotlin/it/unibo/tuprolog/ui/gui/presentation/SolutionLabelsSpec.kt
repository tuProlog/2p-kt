package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import kotlin.time.Duration.Companion.microseconds

class SolutionLabelsSpec :
    FunSpec({
        test("query/legacy-has-no-brackets-and-one-final-dot") {
            queryLabel("member(X, [a])", null) shouldBe "?- member(X, [a])."
            queryLabel("true.  ", null) shouldBe "?- true."
        }
        test("query/local-timestamp") {
            queryLabel("true.", 0L) shouldMatch Regex("""\[\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}\] \?- true\.""")
        }
        test("result/timed-success-and-undecorated-logic") {
            val solution =
                SolutionPresentation.Yes(
                    "p(X).",
                    listOf(BindingPresentation("X", "a")),
                    "p(a)",
                    time = 1234.microseconds,
                )
            solution.resultLabel(1) shouldBe "1. [1.234ms] yes: p(a)"
            solution.logicText shouldBe "p(a)"
            solution.detailLines shouldBe listOf("X = a")
        }
        test("result/untimed-failure-and-timeout") {
            SolutionPresentation.No("fail.").resultLabel(2) shouldBe "2. no"
            val halt = SolutionPresentation.Halt("p.", "expired", listOf("p/0"), true)
            halt.resultLabel(3) shouldBe "3. timeout: expired"
            halt.detailLines shouldBe listOf("at p/0")
        }
    })
