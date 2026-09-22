package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.solve.flags.FlagDomain
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebIdeFlagInputTest {
    private val domain = FlagDomain.IntRange(1, 10)

    @Test
    fun `values within the domain are valid`() {
        assertTrue(isValidIntRangeInput("1", domain))
        assertTrue(isValidIntRangeInput("5", domain))
        assertTrue(isValidIntRangeInput("10", domain))
    }

    @Test
    fun `out-of-range values are rejected even though min max are only browser hints`() {
        assertFalse(isValidIntRangeInput("0", domain))
        assertFalse(isValidIntRangeInput("11", domain))
        assertFalse(isValidIntRangeInput("-100", domain))
    }

    @Test
    fun `blank, fractional, and non-numeric values are rejected`() {
        assertFalse(isValidIntRangeInput("", domain))
        assertFalse(isValidIntRangeInput("3.5", domain))
        assertFalse(isValidIntRangeInput("abc", domain))
    }
}
