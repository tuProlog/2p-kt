package it.unibo.tuprolog.solve

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Test class for [currentTimeInstant] Java implementation
 *
 * @author Enrico
 */
internal class CurrentTimeInstantKtJavaTest {

    @Test
    fun currentTimeReturnsActualTimeInMillis() {
        assertEquals(System.currentTimeMillis(), currentTimeInstant())
    }
}
