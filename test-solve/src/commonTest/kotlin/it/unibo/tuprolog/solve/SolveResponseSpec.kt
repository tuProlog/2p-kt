package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSideEffectManager
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSolution
import it.unibo.tuprolog.solve.testutils.SolveUtils.someSideEffects
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [Solve.Response]
 *
 * @author Enrico
 */
class SolveResponseSpec :
    FunSpec({
        val aResponse =
            Solve.Response(aSolution, aSideEffectManager, someSideEffects)

        test("response-inserted-data-correct") {
            assertEquals(aSolution, aResponse.solution)
            assertEquals(aSideEffectManager, aResponse.sideEffectManager)
            assertEquals(someSideEffects, aResponse.sideEffects)
        }

        test("response-default-values-correct") {
            val toBeTested = Solve.Response(aSolution)

            assertNull(toBeTested.sideEffectManager)
        }

        test("equals-works-as-expected") {
            assertEquals(aResponse.copy(), aResponse)
        }
    })
