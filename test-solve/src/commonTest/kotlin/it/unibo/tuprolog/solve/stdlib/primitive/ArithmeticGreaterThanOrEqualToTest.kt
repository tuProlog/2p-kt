package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.greaterOrEqualQueryToResult
import kotlin.test.Test

/**
 * Test class for [ArithmeticGreaterThanOrEqualTo]
 *
 * @author Enrico
 */

internal class ArithmeticGreaterThanOrEqualToTest {

    @Test
    fun computesCorrectResult() {
        greaterOrEqualQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(ArithmeticGreaterThanOrEqualTo, input, result)
        }
    }
}
