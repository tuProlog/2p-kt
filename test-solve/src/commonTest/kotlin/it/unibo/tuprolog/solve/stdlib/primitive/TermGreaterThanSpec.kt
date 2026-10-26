package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.greaterQueryToResult

/**
 * Test class for [TermGreaterThan]
 *
 */
class TermGreaterThanSpec :
    FunSpec({
        test("computes-correct-result") {
            greaterQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(TermGreaterThan, input, result)
            }
        }
    })
