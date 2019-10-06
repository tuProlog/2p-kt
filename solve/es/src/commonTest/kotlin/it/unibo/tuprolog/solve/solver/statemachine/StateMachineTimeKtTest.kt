package it.unibo.tuprolog.solve.solver.statemachine

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test class for [currentTime] common behaviour
 *
 * @author Enrico
 */
internal class StateMachineTimeKtTest {

    @Test
    fun currentTimeBehavesAsExpected() {
        val startTime = currentTime()
        val timeLag = 300

        val timeInstantsSequence =
                generateSequence(startTime) {
                    currentTime().takeIf { it < startTime + timeLag }
                }.chunked(2)

        assertTrue {
            timeInstantsSequence
                    .fold(true) { timeFlowedAheadTillNow, times ->
                        timeFlowedAheadTillNow && times.first() <= times.last()
                    }
        }
    }

}
