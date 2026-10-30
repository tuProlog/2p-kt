package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.greaterQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticGreaterThan]
 *
 * @author Enrico
 */
class ArithmeticGreaterThanSpec :
    FunSpec({
        test("computes-correct-result") {
            greaterQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(ArithmeticGreaterThan, input, result)
            }
        }
    })
