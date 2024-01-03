package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.lowerQueryToResult
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import kotlin.test.Test

/**
 * Test class for [ArithmeticLowerThan]
 *
 * @author Enrico
 */
internal class ArithmeticLowerThanTest {
    @Test
    fun computesCorrectResult() {
        lowerQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(ArithmeticLowerThan, input, result)
        }
    }
}
