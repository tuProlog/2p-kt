package it.unibo.tuprolog.solve

import org.junit.Test
import kotlin.test.assertTrue

/**
 * Test class for [currentTimeInstant] Java implementation
 *
 * @author Enrico
 */
internal class CurrentTimeInstantKtJavaTest {

    private val TOLERANCE = 10L // 10 ms

    @Test
    fun currentTimeReturnsActualTimeInMillis() {
        val official = System.currentTimeMillis()
        val our = currentTimeInstant()

        assertTrue {
            our - official < TOLERANCE
        }
    }
}
