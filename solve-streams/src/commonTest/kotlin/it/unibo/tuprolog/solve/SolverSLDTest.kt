package it.unibo.tuprolog.solve

import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.PrimitivesUtils
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.testutils.SolverSLDUtils
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [SolverSLD]
 *
 * @author Enrico
 */
internal class SolverSLDTest {

    @Test
    fun defaultConstructorParameters() {
        val toBeTested = SolverSLD()

        assertEquals(Libraries(), toBeTested.libraries)
        assertEquals(emptyMap(), toBeTested.flags)
        assertEquals(ClauseDatabase.empty(), toBeTested.staticKB)
        assertEquals(ClauseDatabase.empty(), toBeTested.dynamicKB)
    }

    @Test
    fun solveStructWorksAsExpected() { // TODO: 08/11/2019 remove this test when all data migrated to common solver testing
        SolverSLDUtils.contextAndRequestToSolutionMap.forEach { (input, expectedOutput) ->
            val (query, startContext) = input

            val toBeTested = with(startContext) { SolverSLD(libraries, flags, staticKB, dynamicKB) }.solve(query).toList()

            assertSolutionEquals(expectedOutput, toBeTested)
        }
    }

    @Test // TODO: 08/11/2019 remove this test when all data migrated to common solver testing
    fun solveSolveRequestIgnoresStartContextAndExecutesDirectlyGivenSolveRequest() {
        SolverSLDUtils.contextAndRequestToSolutionMap.forEach { (input, expectedOutput) ->
            val (query, startContext) = input

            val toBeTested = SolverSLD.solve(Solve.Request(query.extractSignature(), query.argsList, startContext))
                    .map { PrimitivesUtils.filterInterestingVariables(it.solution) }.toList()

            assertSolutionEquals(expectedOutput, toBeTested)
        }
    }

}
