package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.greaterQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import kotlin.test.Test

/**
 * Test class for [ArithmeticGreaterThan]
 *
 * @author Enrico
 */
internal class ArithmeticGreaterThanTest {

    @Test
    fun computesCorrectResult() {
        greaterQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(ArithmeticGreaterThan, input, result)
        }
    }
}
