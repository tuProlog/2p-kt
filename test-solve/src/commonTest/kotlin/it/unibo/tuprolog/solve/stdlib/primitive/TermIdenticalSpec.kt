package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.equalQueryToResult

/**
 * Test class for [TermIdentical]
 *
 */
class TermIdenticalSpec :
    FunSpec({
        test("computes-correct-result") {
            equalQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(TermIdentical, input, result)
            }
        }
    })
