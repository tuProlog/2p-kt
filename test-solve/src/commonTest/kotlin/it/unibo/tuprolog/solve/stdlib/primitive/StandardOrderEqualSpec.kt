package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardOrderEqualSpec :
    FunSpec({
        test("computes-correct-result") {
            TermOrderingUtils.standardOrderEqualTest.forEach { (input, result) ->
                TermOrderingUtils.assertCorrectResponse(TermSame, input, result)
            }
        }
    })
