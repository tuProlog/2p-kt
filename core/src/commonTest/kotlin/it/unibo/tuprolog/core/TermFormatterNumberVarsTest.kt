package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals

class TermFormatterNumberVarsTest {
    private fun format(n: Int): String = TermFormatter.default().format(Struct.of("\$VAR", Integer.of(n)))

    @Test
    fun numberedVariablesHaveNoSuffixInTheirFirstRound() {
        assertEquals(
            listOf("A", "B", "Z", "A1", "B1", "Z1", "A2"),
            listOf(0, 1, 25, 26, 27, 51, 52).map(::format),
        )
    }
}
