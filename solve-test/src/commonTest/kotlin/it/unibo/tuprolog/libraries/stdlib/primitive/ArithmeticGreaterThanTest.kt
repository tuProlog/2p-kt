package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.ArithmeticUtils.assertCorrectResponse
import it.unibo.tuprolog.libraries.stdlib.primitive.testutils.ArithmeticUtils.greaterQueryToResult
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
