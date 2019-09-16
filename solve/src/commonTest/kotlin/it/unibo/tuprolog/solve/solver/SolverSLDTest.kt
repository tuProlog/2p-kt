package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.solve.solver.testutils.SolverSLDUtils
import it.unibo.tuprolog.solve.solver.testutils.SolverSLDUtils.assertSolutionsCorrect
import kotlin.test.Test

/**
 * Test class for [SolverSLD]
 *
 * @author Enrico
 */
internal class SolverSLDTest {

    @Test
    fun solveStructWorksAsExpected() {
        SolverSLDUtils.contextAndRequestToSolutionMap.forEach { (input, expectedOutput) ->
            val (query, startContext) = input

            val toBeTested = SolverSLD(startContext).solve(query).toList()

            assertSolutionsCorrect(expectedOutput, toBeTested)
        }
    }

    // TODO: 16/09/2019 other methods testing

}
