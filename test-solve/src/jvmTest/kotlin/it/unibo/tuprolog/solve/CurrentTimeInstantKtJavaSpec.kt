package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue

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

            (our - official < TOLERANCE).shouldBeTrue()
        }
    })

private const val TOLERANCE = 10L // 10 ms
