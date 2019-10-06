package it.unibo.tuprolog.solve.solver.statemachine

import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [currentTime] Javascript implementation
 *
 * @author Enrico
 */
internal class CurrentTimeKtJsTest {

    @Test
    fun currentTimeReturnsActualTimeInMillis() {
        assertEquals(Date().getTime().toLong(), currentTime())
    }

}
