package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.TermOrderingUtils
import kotlin.test.Test

class StandardLowerThanOrEqualTest {
    @Test
    fun computesCorrectResult() {
        TermOrderingUtils.standardOrderLowerThanOrEqualToTest.forEach { (input, result) ->
            TermOrderingUtils.assertCorrectResponse(TermLowerThanOrEqualTo, input, result)
        }
    }
}
