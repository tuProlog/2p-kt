package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.notEqualQueryToResult
import kotlin.test.Test

/**
 * Test class for [ArithmeticNotEqual]
 *
 * @author Enrico
 */
internal class ArithmeticNotEqualTest {

    @Test
    fun computesCorrectResult() {
        notEqualQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(ArithmeticNotEqual, input, result)
        }
    }

}
