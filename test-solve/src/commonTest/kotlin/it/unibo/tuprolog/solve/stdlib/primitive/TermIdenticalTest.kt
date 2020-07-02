package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.BinaryRelationUtils.assertCorrectResponse
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermUtils.equalQueryToResult
import kotlin.test.Test

/**
 * Test class for [TermIdentical]
 *
 */
internal class TermIdenticalTest {

    @Test
    fun computesCorrectResult() {
        equalQueryToResult.forEach { (input, result) ->
            assertCorrectResponse(TermIdentical, input, result)
        }
    }

}
