package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.ArithmeticUtils.assertCorrectResponse
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.ArithmeticUtils.lowerQueryToResult
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
