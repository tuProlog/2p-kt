package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.lowerQueryToResult

/**
 * Test class for [TermLowerThan]
 *
 */
class TermLowerThanSpec :
    FunSpec({
        test("computes-correct-result") {
            lowerQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(TermLowerThan, input, result)
            }
        }
    })
