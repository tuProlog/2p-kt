package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.greaterOrEqualQueryToResult

/**
 * Test class for [TermGreaterThanOrEqualTo]
 *
 */
class TermGreaterThanOrEqualToSpec :
    FunSpec({
        test("computes-correct-result") {
            greaterOrEqualQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(TermGreaterThanOrEqualTo, input, result)
            }
        }
    })
