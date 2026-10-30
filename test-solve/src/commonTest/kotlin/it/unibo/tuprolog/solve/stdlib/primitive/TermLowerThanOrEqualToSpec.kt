package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.lowerOrEqualQueryToResult

/**
 * Test class for [TermLowerThanOrEqualTo]
 *
 */
class TermLowerThanOrEqualToSpec :
    FunSpec({
        test("computes-correct-result") {
            lowerOrEqualQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(TermLowerThanOrEqualTo, input, result)
            }
        }
    })
