package it.unibo.tuprolog.solve

import it.unibo.tuprolog.libraries.Libraries
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

}
