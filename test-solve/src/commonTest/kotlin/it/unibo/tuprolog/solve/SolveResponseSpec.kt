package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSideEffectManager
import it.unibo.tuprolog.solve.testutils.SolveUtils.aSolution
import it.unibo.tuprolog.solve.testutils.SolveUtils.anExecutionContext
import it.unibo.tuprolog.solve.testutils.SolveUtils.someSideEffects

/**
 * Test class for [Solve.Response]
 *
 * @author Enrico
 */
class SolveResponseSpec :
    FunSpec({
        val aResponse =
            Solve.Response(aSolution, anExecutionContext, aSideEffectManager, someSideEffects)

        test("response-inserted-data-correct") {
            aResponse.solution shouldBe aSolution
            aResponse.context shouldBe anExecutionContext
            aResponse.sideEffectManager shouldBe aSideEffectManager
            aResponse.sideEffects shouldBe someSideEffects
        }

        test("response-default-values-correct") {
            val toBeTested = Solve.Response(aSolution, anExecutionContext)

            toBeTested.sideEffectManager.shouldBeNull()
        }

        test("equals-works-as-expected") {
            aResponse shouldBe aResponse.copy()
        }
    })
