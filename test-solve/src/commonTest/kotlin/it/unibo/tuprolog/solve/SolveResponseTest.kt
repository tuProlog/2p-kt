package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.testutils.SolveUtils.aDynamicKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSideEffectManager
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSolution
import it.unibo.tuprolog.solve.testutils.SolveUtils.aStaticKB
import it.unibo.tuprolog.solve.testutils.SolveUtils.someFlags
import it.unibo.tuprolog.solve.testutils.SolveUtils.someLibraries
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
        Solve.Response(aSolution, someLibraries, someFlags, aStaticKB, aDynamicKB, aSideEffectManager)

    @Test
    fun responseInsertedDataCorrect() {
        assertEquals(aSolution, aResponse.solution)
        assertEquals(someLibraries, aResponse.libraries)
        assertEquals(someFlags, aResponse.flags)
        assertEquals(aStaticKB, aResponse.staticKb)
        assertEquals(aDynamicKB, aResponse.dynamicKb)
        assertEquals(aSideEffectManager, aResponse.sideEffectManager)
    }

    @Test
    fun responseDefaultValuesCorrect() {
        val toBeTested = Solve.Response(aSolution)

        assertNull(toBeTested.libraries)
        assertNull(toBeTested.flags)
        assertNull(toBeTested.staticKb)
        assertNull(toBeTested.dynamicKb)
        assertNull(toBeTested.sideEffectManager)
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(aResponse.copy(), aResponse)
    }

}
