package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.testutils.SolveUtils.aDynamicKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSideEffectManager
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSolution
import it.unibo.tuprolog.solve.testutils.SolveUtils.aStaticKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.someFlags
import it.unibo.tuprolog.solve.testutils.SolveUtils.someLibraries
import it.unibo.tuprolog.solve.testutils.SolveUtils.someSideEffects
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [Solve.Response]
 *
 * @author Enrico
 */
internal class SolveResponseTest {

    private val aResponse =
        Solve.Response(aSolution, aSideEffectManager, someSideEffects)

    @Test
    fun responseInsertedDataCorrect() {
        assertEquals(aSolution, aResponse.solution)
        assertEquals(aSideEffectManager, aResponse.sideEffectManager)
        assertEquals(someSideEffects, aResponse.sideEffects)
    }

    @Test
    fun responseDefaultValuesCorrect() {
        val toBeTested = Solve.Response(aSolution)

        assertNull(toBeTested.sideEffectManager)
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(aResponse.copy(), aResponse)
    }

}
