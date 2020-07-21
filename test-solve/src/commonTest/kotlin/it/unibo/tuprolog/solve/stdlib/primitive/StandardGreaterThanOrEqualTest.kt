package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.StandardOperatorUtils
import kotlin.test.Test

internal class StandardGreaterThanOrEqualTest {
    @Test
    fun computesCorrectResult() {
        StandardOperatorUtils.standardOrderGreaterThanOrEqualToTest.forEach { (input, result) ->
            StandardOperatorUtils.assertCorrectResponse(StandardOrderGreaterThanOrEqualTo, input, result)
        }
    }
}