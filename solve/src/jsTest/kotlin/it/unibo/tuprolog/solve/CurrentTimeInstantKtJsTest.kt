package it.unibo.tuprolog.solve

import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [currentTimeInstant] Javascript implementation
 *
 * @author Enrico
 */
internal class CurrentTimeInstantKtJsTest {

    @Test
    fun currentTimeReturnsActualTimeInMillis() {
        assertEquals(Date().getTime().toLong(), currentTimeInstant())
    }

}
