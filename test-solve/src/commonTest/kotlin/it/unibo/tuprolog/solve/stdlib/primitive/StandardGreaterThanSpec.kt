package it.unibo.tuprolog.solve.stdlib.primitive
import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils

class StandardGreaterThanSpec :
    FunSpec({
        test("computes-correct-result") {
            TermOrderingUtils.standardOrderGreaterThanTest.forEach { (input, result) ->
                TermOrderingUtils.assertCorrectResponse(TermGreaterThan, input, result)
            }
        }
    })
