package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.lowerOrEqualQueryToResult
import kotlin.test.Test

/**
 * Test class for [TermLowerThanOrEqualTo]
 *
 */
internal class TermLowerThanOrEqualToTest {
    @Test
    fun computesCorrectResult() {
        lowerOrEqualQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(TermLowerThanOrEqualTo, input, result)
        }
    }
}
