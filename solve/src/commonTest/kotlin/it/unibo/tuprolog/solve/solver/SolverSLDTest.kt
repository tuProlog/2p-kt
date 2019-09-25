package it.unibo.tuprolog.solve.solver

import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.Solve
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

    @Test
    fun solveSolveRequestIgnoresStartContextAndExecutesDirectlyGivenSolveRequest() {
        SolverSLDUtils.contextAndRequestToSolutionMap.forEach { (input, expectedOutput) ->
            val (query, startContext) = input

            val toBeTested = SolverSLD().solve(Solve.Request(query.extractSignature(), query.argsList, query, startContext))
                    .map { it.solution }.toList()

            assertSolutionsCorrect(expectedOutput, toBeTested)
        }
    }

}
