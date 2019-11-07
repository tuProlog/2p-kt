package it.unibo.tuprolog.solve

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.PrimitivesUtils
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.testutils.SolverSLDUtils
import it.unibo.tuprolog.solve.testutils.SolverSLDUtils.assertSolutionsCorrect
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

            val toBeTested = with(startContext) { SolverSLD(libraries, flags, staticKB, dynamicKB) }.solve(query).toList()

            assertSolutionsCorrect(expectedOutput, toBeTested)
        }
    }

    @Test
    fun solveSolveRequestIgnoresStartContextAndExecutesDirectlyGivenSolveRequest() {
        SolverSLDUtils.contextAndRequestToSolutionMap.forEach { (input, expectedOutput) ->
            val (query, startContext) = input

            val toBeTested = SolverSLD.solve(Solve.Request(query.extractSignature(), query.argsList, startContext))
                    .map { PrimitivesUtils.filterInterestingVariables(it.solution) }.toList()

            assertSolutionsCorrect(expectedOutput, toBeTested)
        }
    }

}
