package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertTrue

/**
 * Test class for [currentTimeInstant] common behaviour
 *
 * @author Enrico
 */
class TimeRepresentationKtSpec :
    FunSpec({
        test("current-time-behaves-as-expected") {
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
    })
