package it.unibo.tuprolog.solve

import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test class for [currentTimeInstant] Javascript implementation
 *
 * @author Enrico
 */
internal class CurrentTimeInstantKtJsTest {

    private val TOLERANCE = 10L // 10 ms

    @Test
    fun currentTimeReturnsActualTimeInMillis() {
        val official = Date().getTime().toLong()
        val our = currentTimeInstant()

        assertTrue {
            our - official < TOLERANCE
        }
    }

}
