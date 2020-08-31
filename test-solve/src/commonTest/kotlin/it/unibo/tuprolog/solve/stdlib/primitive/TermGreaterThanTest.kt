package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.greaterQueryToResult
import kotlin.test.Test

/**
 * Test class for [TermGreaterThan]
 *
 */
internal class TermGreaterThanTest {

    @Test
    fun computesCorrectResult() {
        greaterQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(TermGreaterThan, input, result)
        }
    }

}
