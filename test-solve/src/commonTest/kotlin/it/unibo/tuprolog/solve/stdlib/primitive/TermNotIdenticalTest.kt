package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.notEqualQueryToResult
import kotlin.test.Test

/**
 * Test class for [ArithmeticNotEqual]
 *
 */
internal class TermNotIdenticalTest {

    @Test
    fun computesCorrectResult() {
        notEqualQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(TermNotIdentical, input, result)
        }
    }

}
