package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.StandardOperatorUtils
import kotlin.test.Test

internal class StandardLowerThanTest {
    @Test
    fun computesCorrectResult() {
        StandardOperatorUtils.standardOrderLowerThanTest.forEach { (input, result) ->
            StandardOperatorUtils.assertCorrectResponse(StandardOrderLowerThan, input, result)
        }
    }
}