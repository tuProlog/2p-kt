package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertTrue

/**
 * Test class for [currentTimeInstant] Java implementation
 *
 * @author Enrico
 */
class CurrentTimeInstantKtJavaSpec :
    FunSpec({
        test("current-time-returns-actual-time-in-millis") {
            val official = System.currentTimeMillis()
            val our = currentTimeInstant()

            assertTrue {
                our - official < TOLERANCE
            }
        }
    })

private const val TOLERANCE = 10L // 10 ms
