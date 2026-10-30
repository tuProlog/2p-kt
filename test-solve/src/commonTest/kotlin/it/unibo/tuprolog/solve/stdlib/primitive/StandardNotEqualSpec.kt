package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardNotEqualSpec :
    FunSpec({
        test("computes-correct-result") {
            TermOrderingUtils.standardOrderNotEqualTest.forEach { (input, result) ->
                TermOrderingUtils.assertCorrectResponse(TermNotSame, input, result)
            }
        }
    })
