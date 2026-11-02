package it.unibo.tuprolog.ui.gui.plp

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PercentageFormatSpec :
    FunSpec({
        test("whole percentages drop the decimal part") {
            formatProbabilityPercentage(0.25) shouldBe "25%"
            formatProbabilityPercentage(0.0) shouldBe "0%"
            formatProbabilityPercentage(1.0) shouldBe "100%"
        }

        test("repeating decimals are kept up to six digits") {
            formatProbabilityPercentage(1.0 / 3.0) shouldBe "33.333333%"
        }
    })
