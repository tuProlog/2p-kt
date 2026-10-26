package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.notEqualQueryToResult

/**
 * Test class for [ArithmeticNotEqual]
 *
 */
class TermNotIdenticalSpec :
    FunSpec({
        test("computes-correct-result") {
            notEqualQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(TermNotIdentical, input, result)
            }
        }
    })
