package it.unibo.tuprolog.solve.library.stdlib.primitive

import it.unibo.tuprolog.solve.library.stdlib.primitive.testutils.ArithmeticUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.library.stdlib.primitive.testutils.ArithmeticUtils.lowerOrEqualQueryToResult
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
