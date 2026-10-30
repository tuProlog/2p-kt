package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.lowerQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticLowerThan]
 *
 * @author Enrico
 */
class ArithmeticLowerThanSpec :
    FunSpec({
        test("computes-correct-result") {
            lowerQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(ArithmeticLowerThan, input, result)
            }
        }
    })
