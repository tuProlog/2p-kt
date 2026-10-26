package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardGreaterThanOrEqualSpec :
    FunSpec({
        test("computes-correct-result") {
            TermOrderingUtils.standardOrderGreaterThanOrEqualToTest.forEach { (input, result) ->
                TermOrderingUtils.assertCorrectResponse(TermGreaterThanOrEqualTo, input, result)
            }
        }
    })
