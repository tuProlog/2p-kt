package it.unibo.tuprolog.ui.gui.plp

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals

class PercentageFormatSpec :
    FunSpec({
        test("whole percentages drop the decimal part") {
            assertEquals("25%", formatProbabilityPercentage(0.25))
            assertEquals("0%", formatProbabilityPercentage(0.0))
            assertEquals("100%", formatProbabilityPercentage(1.0))
        }

        test("repeating decimals are kept up to six digits") {
            assertEquals("33.333333%", formatProbabilityPercentage(1.0 / 3.0))
        }
    })
