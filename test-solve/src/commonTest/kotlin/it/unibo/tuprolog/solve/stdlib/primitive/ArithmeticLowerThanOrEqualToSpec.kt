package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.lowerOrEqualQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticLowerThanOrEqualTo]
 *
 * @author Enrico
 */
class ArithmeticLowerThanOrEqualToSpec :
    FunSpec({
        test("computes-correct-result") {
            lowerOrEqualQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(ArithmeticLowerThanOrEqualTo, input, result)
            }
        }
    })
