package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils
import kotlin.test.Test

internal class StandardGreaterThanOrEqualTest {
    @Test
    fun computesCorrectResult() {
        TermOrderingUtils.standardOrderGreaterThanOrEqualToTest.forEach { (input, result) ->
            TermOrderingUtils.assertCorrectResponse(TermGreaterThanOrEqualTo, input, result)
        }
    }
}