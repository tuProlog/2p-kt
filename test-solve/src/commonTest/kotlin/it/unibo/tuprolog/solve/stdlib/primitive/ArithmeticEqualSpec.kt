package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.equalQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticEqual]
 *
 * @author Enrico
 */
class ArithmeticEqualSpec :
    FunSpec({
        test("computes-correct-result") {
            equalQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(ArithmeticEqual, input, result)
            }
        }
    })
