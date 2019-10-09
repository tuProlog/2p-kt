package it.unibo.tuprolog.solve

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test class for [currentTimeInstant] common behaviour
 *
 * @author Enrico
 */
internal class TimeRepresentationKtTest {

    @Test
    fun currentTimeBehavesAsExpected() {
        val startTime = currentTimeInstant()
        val timeLag = 300

        val timeInstantsSequence =
                generateSequence(startTime) {
                    currentTimeInstant().takeIf { it < startTime + timeLag }
                }.chunked(2)

        assertTrue {
            timeInstantsSequence
                    .fold(true) { timeFlowedAheadTillNow, times ->
                        timeFlowedAheadTillNow && times.first() <= times.last()
                    }
        }
    }

}
