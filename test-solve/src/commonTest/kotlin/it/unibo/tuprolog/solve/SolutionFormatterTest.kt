package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlin.test.Test
import kotlin.test.assertEquals

/** Test class for [SolutionFormatter], focused on the `GroundQueriesHaveBooleanSolution` presentation flag. */
internal class SolutionFormatterTest {
    private val scope = Scope.empty()
    private val groundQuery =
        with(scope) { Struct.of("is", Integer.of(2), Struct.of("+", Integer.of(1), Integer.of(1))) }
    private val nonGroundQuery =
        with(scope) { Struct.of("is", varOf("X"), Struct.of("+", Integer.of(1), Integer.of(1))) }

    @Test
    fun `by default a ground query's yes solution still shows the solved query`() {
        val formatter = SolutionFormatter.of()
        val solution = Solution.yes(groundQuery)

        assertEquals("yes: 2 is 1 + 1\n    ", formatter.format(solution))
    }

    @Test
    fun `groundQueriesHaveBooleanSolution renders a ground query's yes solution as just yes`() {
        val formatter = SolutionFormatter.of(groundQueriesHaveBooleanSolution = true)
        val solution = Solution.yes(groundQuery)

        assertEquals("yes.", formatter.format(solution))
    }

    @Test
    fun `groundQueriesHaveBooleanSolution leaves a non-ground query's yes solution unaffected`() {
        val formatter = SolutionFormatter.of(groundQueriesHaveBooleanSolution = true)
        val solution =
            with(scope) {
                Solution.yes(nonGroundQuery, Substitution.of(mapOf(varOf("X") to Integer.of(2))))
            }

        assertEquals("yes: 2 is 1 + 1\n    X = 2", formatter.format(solution))
    }

    @Test
    fun `groundQueriesHaveBooleanSolution does not affect no or halt solutions`() {
        val formatter = SolutionFormatter.of(groundQueriesHaveBooleanSolution = true)

        assertEquals("no.", formatter.format(Solution.no(groundQuery)))
    }
}
