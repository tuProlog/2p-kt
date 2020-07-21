package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.stdlib.primitive.testutils.StandardOperatorUtils
import kotlin.test.Test

internal class StandardOrderEqualTest {

    @Test
    fun computesCorrectResult() {
        StandardOperatorUtils.standardOrderEqualTest.forEach { (input, result) ->
            StandardOperatorUtils.assertCorrectResponse(StandardOrderEqual, input, result)
        }
    }
}