package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.greaterOrEqualQueryToResult
import kotlin.test.Test

/**
 * Test class for [TermGreaterThanOrEqualTo]
 *
 */
internal class TermGreaterThanOrEqualToTest {
    @Test
    fun computesCorrectResult() {
        greaterOrEqualQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(TermGreaterThanOrEqualTo, input, result)
        }
    }
}
