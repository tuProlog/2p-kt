package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.testutils.SolveUtils.aDynamicKB
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

    private val aResponse = Solve.Response(aSolution, someLibraries, someFlags, aStaticKB, aDynamicKB)

    @Test
    fun responseInsertedDataCorrect() {
        assertEquals(aSolution, aResponse.solution)
        assertEquals(someLibraries, aResponse.libraries)
        assertEquals(someFlags, aResponse.flags)
        assertEquals(aStaticKB, aResponse.staticKB)
        assertEquals(aDynamicKB, aResponse.dynamicKB)
    }

    @Test
    fun responseDefaultValuesCorrect() {
        val toBeTested = Solve.Response(aSolution)

        assertNull(toBeTested.libraries)
        assertNull(toBeTested.flags)
        assertNull(toBeTested.staticKB)
        assertNull(toBeTested.dynamicKB)
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(aResponse.copy(), aResponse)
    }

}
