package it.unibo.tuprolog.ui.gui.presentation

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.microseconds

class SolutionPresentationSpec :
    FunSpec({
        test("time-annotation/untimed-solution-has-none") {
            SolutionPresentation.No("q.").timeAnnotation shouldBe ""
            listOf(SolutionPresentation.No("q.")).totalTimeAnnotation shouldBe ""
        }

        test("time-annotation/total-sums-the-timed-solutions") {
            val solutions =
                listOf(
                    SolutionPresentation.Yes("q.", time = 1000.microseconds),
                    SolutionPresentation.No("q.", time = 234.microseconds),
                )
            solutions.first().timeAnnotation shouldBe " (1ms)"
            solutions.totalTimeAnnotation shouldBe " (1.234ms)"
        }
    })
