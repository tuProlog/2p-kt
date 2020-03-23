package it.unibo.tuprolog.solve.library.stdlib.primitive

import it.unibo.tuprolog.solve.library.stdlib.primitive.testutils.ArithmeticUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.library.stdlib.primitive.testutils.ArithmeticUtils.lowerQueryToResult
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
