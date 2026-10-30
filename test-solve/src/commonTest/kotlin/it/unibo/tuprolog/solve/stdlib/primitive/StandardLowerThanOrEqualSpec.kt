package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardLowerThanOrEqualSpec :
    FunSpec({
        test("computes-correct-result") {
            TermOrderingUtils.standardOrderLowerThanOrEqualToTest.forEach { (input, result) ->
                TermOrderingUtils.assertCorrectResponse(TermLowerThanOrEqualTo, input, result)
            }
        }
    })
