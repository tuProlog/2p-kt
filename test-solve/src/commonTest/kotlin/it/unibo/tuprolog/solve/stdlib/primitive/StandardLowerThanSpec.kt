package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardLowerThanSpec :
    FunSpec({
        test("computes-correct-result") {
            TermOrderingUtils.standardOrderLowerThanTest.forEach { (input, result) ->
                TermOrderingUtils.assertCorrectResponse(TermLowerThan, input, result)
            }
        }
    })
