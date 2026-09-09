package it.unibo.tuprolog.ui.gui.plp

import kotlin.test.Test
import kotlin.test.assertEquals

class PercentageFormatTest {
    @Test
    fun `whole percentages drop the decimal part`() {
        assertEquals("25%", formatProbabilityPercentage(0.25))
        assertEquals("0%", formatProbabilityPercentage(0.0))
        assertEquals("100%", formatProbabilityPercentage(1.0))
    }

    @Test
    fun `repeating decimals are kept up to six digits`() {
        assertEquals("33.333333%", formatProbabilityPercentage(1.0 / 3.0))
    }
}
