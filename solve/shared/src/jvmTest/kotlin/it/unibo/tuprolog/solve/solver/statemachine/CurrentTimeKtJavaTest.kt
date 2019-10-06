package it.unibo.tuprolog.solve.solver.statemachine

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Test class for [currentTime] Java implementation
 *
 * @author Enrico
 */
internal class CurrentTimeKtJavaTest {

    @Test
    fun currentTimeReturnsActualTimeInMillis() {
        assertEquals(System.currentTimeMillis(), currentTime())
    }
}
