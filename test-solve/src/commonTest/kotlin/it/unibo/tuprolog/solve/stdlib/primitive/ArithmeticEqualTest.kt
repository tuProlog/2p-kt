package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.equalQueryToResult
import kotlin.test.Test

/**
 * Test class for [ArithmeticEqual]
 *
 * @author Enrico
 */
internal class ArithmeticEqualTest {

    @Test
    fun computesCorrectResult() {
        equalQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(ArithmeticEqual, input, result)
        }
    }

}
