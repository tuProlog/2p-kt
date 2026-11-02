package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.js.Date

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

            (our - official < TOLERANCE).shouldBeTrue()
        }
    })

private const val TOLERANCE = 10L // 10 ms
