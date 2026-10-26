package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import kotlin.js.Date
import kotlin.test.assertTrue

/**
 * Test class for [currentTimeInstant] Javascript implementation
 *
 * @author Enrico
 */
class CurrentTimeInstantKtJsSpec :
    FunSpec({
        test("current-time-returns-actual-time-in-millis") {
            val official = Date().getTime().toLong()
            val our = currentTimeInstant()

            assertTrue {
                our - official < TOLERANCE
            }
        }
    })

private const val TOLERANCE = 10L // 10 ms
