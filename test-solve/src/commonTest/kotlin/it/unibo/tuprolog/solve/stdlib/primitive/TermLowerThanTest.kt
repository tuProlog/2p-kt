package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.lowerQueryToResult
import kotlin.test.Test

/**
 * Test class for [TermLowerThan]
 *
 */
internal class TermLowerThanTest {
    @Test
    fun computesCorrectResult() {
        lowerQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(TermLowerThan, input, result)
        }
    }
}
