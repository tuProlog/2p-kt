package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.StandardOperatorUtils
import kotlin.test.Test

class StandardLowerThanOrEqualTest {
    @Test
    fun computesCorrectResult() {
        StandardOperatorUtils.standardOrderLowerThanOrEqualToTest.forEach { (input, result) ->
            StandardOperatorUtils.assertCorrectResponse(StandardOrderLowerThanOrEqualTo, input, result)
        }
    }
}