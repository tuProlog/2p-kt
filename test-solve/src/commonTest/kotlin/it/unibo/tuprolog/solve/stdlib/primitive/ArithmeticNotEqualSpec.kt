package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.notEqualQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse

/**
 * Test class for [ArithmeticNotEqual]
 *
 * @author Enrico
 */
class ArithmeticNotEqualSpec :
    FunSpec({
        test("computes-correct-result") {
            notEqualQueryToResult.forEach { (input, result) ->
                assertCorrectResponse(ArithmeticNotEqual, input, result)
            }
        }
    })
