package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.ui.gui.presentation.BindingPresentation
import it.unibo.tuprolog.ui.gui.presentation.SolutionPresentation
import kotlin.test.Test
import kotlin.test.assertEquals

class WebIdeSolutionLineTest {
    @Test
    fun `a yes solution with a solved query is rendered with it`() {
        val solution = SolutionPresentation.Yes(query = "member(X, [a]).", solvedQuery = "member(a, [a])")

        assertEquals("1. yes: member(a, [a])", solutionLineText(1, solution))
    }

    @Test
    fun `a yes solution with bindings appends them, one per line`() {
        val solution =
            SolutionPresentation.Yes(
                query = "member(X, [a]).",
                bindings = listOf(BindingPresentation("X", "a")),
                solvedQuery = "member(a, [a])",
            )

        assertEquals("1. yes: member(a, [a])\n  X = a", solutionLineText(1, solution))
    }

    @Test
    fun `a null solvedQuery is rendered as a bare yes, for GroundQueriesHaveBooleanSolution`() {
        val solution = SolutionPresentation.Yes(query = "2 is 1 + 1.", solvedQuery = null)

        assertEquals("1. yes.", solutionLineText(1, solution))
    }

    @Test
    fun `a no solution is rendered as no`() {
        assertEquals("2. no", solutionLineText(2, SolutionPresentation.No("fail.")))
    }
}
