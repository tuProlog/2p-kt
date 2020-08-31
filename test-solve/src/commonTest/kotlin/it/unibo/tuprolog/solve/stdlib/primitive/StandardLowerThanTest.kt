package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils
import kotlin.test.Test

internal class StandardLowerThanTest {
    @Test
    fun computesCorrectResult() {
        TermOrderingUtils.standardOrderLowerThanTest.forEach { (input, result) ->
            TermOrderingUtils.assertCorrectResponse(TermLowerThan, input, result)
        }
    }
}