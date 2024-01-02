package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.lowerOrEqualQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import kotlin.test.Test

/**
 * Test class for [ArithmeticLowerThanOrEqualTo]
 *
 * @author Enrico
 */
internal class ArithmeticLowerThanOrEqualToTest {
    @Test
    fun computesCorrectResult() {
        lowerOrEqualQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(ArithmeticLowerThanOrEqualTo, input, result)
        }
    }
}
